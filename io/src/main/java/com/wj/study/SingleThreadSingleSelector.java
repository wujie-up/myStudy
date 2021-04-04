package com.wj.study;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class SingleThreadSingleSelector {

    private static Selector selector;

    public static void main(String[] args) throws Exception {
        // 创建服务端
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(9090));
        // 多路复用器
        selector = Selector.open();
        // 注册到多路复用器， 监听连接事件
        server.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int num = selector.select();
            if (num > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    handleKey(key);
                }
            }
        }
    }

    private static void handleKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            System.out.println("有客户端连接事件...");
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
            client.register(selector, SelectionKey.OP_READ, byteBuffer);
        } else if (key.isReadable()) {
            System.out.println("有客户端读事件...");
            ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
            SocketChannel client = (SocketChannel) key.channel();
            byteBuffer.clear();
            int read = 0;
            while (true) {
                read = client.read(byteBuffer);
                if (read > 0) {
                    byteBuffer.flip();
                    while (byteBuffer.hasRemaining()) {
                        client.write(byteBuffer);
                    }
                } else if (read == 0) {
                    break;
                } else {
                    client.close();
                    break;
                }
            }
        }
    }
}
