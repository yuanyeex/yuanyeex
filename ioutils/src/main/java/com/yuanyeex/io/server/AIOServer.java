package com.yuanyeex.io.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIOServer {

    public static void main(String[] args) {
        new AIOServer(9999)
                .listen();
    }

    private int port;

    public AIOServer(int port) {
        this.port = port;
        listen();
    }

    private void listen() {
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            final AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup
                    .withCachedThreadPool(executorService, 1);
            final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(channelGroup);
            server.bind(new InetSocketAddress(port));
            System.out.println("Server started, listen at: " + port);

            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    System.out.println("I/O success, start process data!");
                    final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                    try {
                        buffer.clear();
                        result.read(buffer).get();
                        buffer.flip();
                        result.write(buffer);
                        buffer.flip();
                    } catch (Exception e) {
                        System.out.println("Process failed with exception: ");
                        e.printStackTrace();
                    } finally {
                        try {
                            result.close();
                        } catch (Exception e) {
                            System.out.println("Close result failed: ");
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("I/O failed:" + exc);
                    exc.printStackTrace();
                }
            });

            while (server.isOpen()) {
                Thread.sleep(5000);
            }

            System.out.println("Server exit");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
