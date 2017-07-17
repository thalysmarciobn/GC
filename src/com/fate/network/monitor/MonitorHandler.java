package com.fate.network.monitor;

import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MonitorHandler extends WebSocketServer {

    private static Logger LOGGER = Logger.getLogger(MonitorHandler.class);

    private Monitor monitor;

    public MonitorHandler(Monitor monitor, int port) {
        super(new InetSocketAddress(port));
        this.monitor = monitor;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        this.monitor.addConnection(conn);
        this.monitor.sendAll("New connection: " + (conn.getRemoteSocketAddress()).getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        this.monitor.removeConnection(conn);
        this.monitor.sendAll("Close client: " + (conn.getRemoteSocketAddress()).getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
    }

}
