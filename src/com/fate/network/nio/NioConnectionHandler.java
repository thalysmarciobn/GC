package com.fate.network.nio;

import com.fate.FateEmulator;
import com.fate.config.Configuration;
import com.fate.world.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioConnectionHandler {

    private SocketChannel socketChannel;
    private ByteBuffer readBuffer;
    private Player player;

    private String ipAddress = "0.0.0.0";

    public NioConnectionHandler(SocketChannel socketChannel) {
        this.player = new Player(this);
        this.socketChannel = socketChannel;
        this.readBuffer = ByteBuffer.allocateDirect(Configuration.c_maxMsgLen);
        try {
            this.ipAddress = ((InetSocketAddress) this.socketChannel.getRemoteAddress()).getAddress().getHostAddress();
        } catch (IOException e) {

        }
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public ByteBuffer getReadBuffer() {
        return this.readBuffer;
    }

    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }

    public void dispatch(ByteBuffer readBuffer) {
        FateEmulator.getInstance().getAbstractExtension().dispacth(this.player, readBuffer, this.player.channelId, this.player.roomId);
    }
}
