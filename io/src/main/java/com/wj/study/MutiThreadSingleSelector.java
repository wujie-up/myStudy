package com.wj.study;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class MutiThreadSingleSelector {

    private static Selector selector;

    public static void main(String[] args) throws Exception {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(9090));

        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int num = selector.select();
            if (num > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
//                        key.cancel();
                        asyncRead(key);
                    }
                }
            }
        }
    }

    private static void asyncRead(SelectionKey key) {
        new Thread(() -> {
            try {
                System.out.println("读事件....");
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
            } catch (IOException e) {

            }
        }).start();
    }

    private static void accept(SelectionKey key) {
        try {
            System.out.println("连接事件....");
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2048);
            client.register(selector, SelectionKey.OP_READ, byteBuffer);
        } catch (IOException e) {
        }
    }
}
