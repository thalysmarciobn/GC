package com.fate.world;

import com.fate.network.nio.NioConnectionHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class Player {

    private NioConnectionHandler nioConnectionHandler;
    public HashMap variables;

    public int channelId = -1;
    public int roomId = -1;

    public Player(NioConnectionHandler nioChannelHandler) {
        this.nioConnectionHandler = nioChannelHandler;
        this.variables = new HashMap();
    }

    public String getIpAddress() {
        return this.nioConnectionHandler.getIpAddress();
    }

    public void dispatch(byte[] data) throws IOException {
        this.nioConnectionHandler.getSocketChannel().write(ByteBuffer.wrap(data));
    }
}
