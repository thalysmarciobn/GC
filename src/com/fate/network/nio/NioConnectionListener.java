package com.fate.network.nio;

import com.fate.FateEmulator;
import com.fate.config.Configuration;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioConnectionListener implements Runnable {

    private ServerSocketChannel serverSocketChannel;
    private Selector OP_ACCEPT;
    private Selector OP_READ;

    public void bind(ServerSocketChannel serverSocketChannel, Selector OP_ACCEPT, Selector OP_READ) {
        this.serverSocketChannel = serverSocketChannel;
        this.OP_ACCEPT = OP_ACCEPT;
        this.OP_READ = OP_READ;
    }

    @Override
    public void run() {
        while (this.serverSocketChannel.isOpen()) {
            try {
                this.OP_ACCEPT.select();
                Iterator<SelectionKey> iterator = this.OP_ACCEPT.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        if (FateEmulator.getInstance().getClientCount() < Configuration.c_maxConnections) {
                            this.register();
                        }
                    }
                }
                Thread.sleep(Configuration.c_elapsedTime);
            } catch (Exception e) {

            }
        }
    }

    private void register() {
        try {
            SocketChannel socketChannel = this.serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            SelectionKey key = socketChannel.register(this.OP_READ, SelectionKey.OP_READ);
            NioConnectionHandler nioConnectionHandler = new NioConnectionHandler(socketChannel);
            key.attach(nioConnectionHandler);
            FateEmulator.getInstance().addClient(socketChannel, nioConnectionHandler);
        } catch (Exception e) {

        }
    }
}
