package com.fate.network.nio;

import com.fate.FateEmulator;
import com.fate.config.Configuration;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioConnectionReader implements Runnable {

    private Selector OP_READ;

    public void bind(Selector selector) {
        this.OP_READ = selector;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.OP_READ.selectNow();
                Iterator<SelectionKey> iterator = this.OP_READ.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        NioConnectionHandler nioConnectionHandler = (NioConnectionHandler) key.attachment();
                        ByteBuffer readBuffer = nioConnectionHandler.getReadBuffer();
                        readBuffer.clear();
                        long len = socketChannel.read(readBuffer);
                        if (len > -1) {
                            readBuffer.flip();
                            nioConnectionHandler.dispatch(readBuffer);
                            readBuffer.clear();
                        } else {
                            key.cancel();
                            FateEmulator.getInstance().removeClient(socketChannel, nioConnectionHandler);
                        }
                    }
                }
                Thread.sleep(Configuration.c_elapsedTime);
            } catch (Exception e) {

            }
        }
    }
}
