package com.fate.network.monitor;

import com.fate.config.Configuration;
import org.java_websocket.WebSocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Monitor implements Runnable {

    private MonitorHandler monitorHandler;
    private LinkedList<WebSocket> connections = new LinkedList();

    public void run() {
        this.monitorHandler = new MonitorHandler(this, Configuration.ws_port);
        this.monitorHandler.start();
    }

    public void addConnection(WebSocket webSocket) {
        synchronized (connections) {
            this.connections.add(webSocket);
        }
    }

    public void removeConnection(WebSocket webSocket) {
        synchronized (this.connections) {
            this.connections.remove(webSocket);
        }
    }

    public void sendAll(String msg) {
        for (WebSocket webSocket : this.connections) {
            webSocket.send(this.getDate() + " - " + Monitor.class.toString() + " - " + msg);
        }
    }

    private String getDate() {
        return new SimpleDateFormat("yyyy/mm/dd HH:mm:ss").format(new Date()).toString();
    }
}
