package com.wj.study.myNetty;

import com.wj.study.myNetty.channel.NettyChannel;
import lombok.Data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class NettyThread extends Thread{
    private Selector selector;
    private NettyGroup group;
    private BlockingQueue<NettyChannel> tasks = new LinkedBlockingQueue<>();
    /**
     * 用来 存储 通信channel 和 我们自定义channel的联系
     */
    ConcurrentHashMap<Channel, NettyChannel> channelMap = new ConcurrentHashMap<>();
    AtomicInteger index = new AtomicInteger(0);

    public NettyThread(NettyGroup group) {
        try {
            // 传入当前组，是为了在接收到连接后，将client传给worker线程处理
            this.group = group;
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (selector.select() > 0) { // 如果没有事件发生，这里会阻塞
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        if (key.isAcceptable()) {
                            handleAccept(key);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        }
                    }
                }

                processTasks();
            } catch (Exception e) {

            }
        }

    }


    /**
     * 处理 注册 任务
     */
    private void processTasks() throws Exception {
        while (!tasks.isEmpty()) {
            NettyChannel nettyChannel = tasks.take();
            Channel channel = nettyChannel.channel();
            if (channel instanceof ServerSocketChannel) {
                ServerSocketChannel server = (ServerSocketChannel) channel;
                server.register(selector, SelectionKey.OP_ACCEPT);
            } else if (channel instanceof SocketChannel) {
                SocketChannel client = (SocketChannel) channel;
                ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
                client.register(selector, SelectionKey.OP_READ, byteBuffer);
            }
            // 初始化channel的管道
            pipelineInit(nettyChannel);
        }
    }

    private void pipelineInit(NettyChannel channel) {
        channelMap.putIfAbsent(channel.channel(), channel);
        channel.pipeline().init(channel);
    }

    /**
     * 只有worker线程才会执行此方法
     */
    private void handleRead(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
        SocketChannel client = (SocketChannel) key.channel();
        byteBuffer.clear();
        int read = client.read(byteBuffer);
        if (read > 0) {
            // todo 读取到数据后，将数据保存到业务层的协议，然后异步处理，当前线程只做IO读取
            channelMap.get(client).pipeline().afterRead(channelMap.get(client), byteBuffer);
            byteBuffer.clear();
        } else if (read < 0) {
            channelMap.get(client).pipeline().close(channelMap.get(client));
        }
    }

    /**
     * 只有boss线程才会执行此方法
     */
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        // 拿到服务端的NettyChannel中的pipeline，执行afterAccept方法
        NettyChannel clientChannel = new NettyChannel(client, new Pipeline(group.getBootStrap().getInitChannelHandler()));
        channelMap.get(server).pipeline().afterAccept(clientChannel);
        // 将client 注册到 worker线程组的 selector上
        NettyThread worker = getWorkerThread(group);
        try {
            // 给worker线程添加任务：1、注册client到selector，2、初始化pipeline
            worker.getTasks().put(clientChannel);
            // 唤醒worker线程去处理上面的任务
            worker.getSelector().wakeup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private NettyThread getWorkerThread(NettyGroup group) {
        int num = index.getAndIncrement();
        int i = num % group.getWorker().getThreads().length;
        return group.getWorker().getThreads()[i];
    }
}
