package com.wj.study;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MutiThreadMutiSelector {

    private static List<ServerSocketChannel> serverList;
    private static List<Selector> selectorList;
    private static AtomicInteger index = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        int num = 3;
        initServers(num);
        register(num);
        start(num);
    }

    private static void start(int num) {
        for (int i = 0; i < num; i++) {
            Selector selector = selectorList.get(i);
            new Thread(() -> doSelect(selector)).start();
        }
    }

    private static void doSelect(Selector selector) {
        while (true) {
            try {
                if (selector.select() > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        handle(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            handleAccept(key);
        } else if (key.isReadable()) {
            handleRead(key);
        }
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer bf = (ByteBuffer) key.attachment();

        int read = client.read(bf);
        if(read > 0) {
            System.out.println( client.getRemoteAddress() + ": " + new String(bf.array()));
            bf.flip();
            while (bf.hasRemaining()) {
                client.write(bf);
            }
            bf.clear();
        } else {
            client.close();
        }
    }

    private static void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        System.out.println("客户端连接：" + client.getRemoteAddress());
        client.configureBlocking(false);
        Selector selector = key.selector();
        ByteBuffer bf = ByteBuffer.allocate(1024);
        client.register(selector, SelectionKey.OP_READ, bf);
    }

    private static void register(int num) throws IOException {
        selectorList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Selector selector = Selector.open();
            ServerSocketChannel server = serverList.get(i);
            server.register(selector, SelectionKey.OP_ACCEPT);
            selectorList.add(selector);
        }
    }

    private static void initServers(int num) throws IOException {
        int initPort = 9090;
        serverList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(initPort++));
            serverList.add(server);
        }
    }
}
