package com.fate;

import com.fate.config.Configuration;
import com.fate.storage.Database;
import com.fate.network.nio.NioConnectionHandler;
import com.fate.network.nio.NioConnectionListener;
import com.fate.network.nio.NioConnectionReader;
import com.fate.network.monitor.Monitor;
import com.fate.extension.AbstractExtension;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FateEmulator {
    private static Logger LOGGER = Logger.getLogger(FateEmulator.class);
    private static ScheduledExecutorService TASK = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private static FateEmulator instance;

    private long startTimeMillis;

    private HashMap<SocketChannel, NioConnectionHandler> clients;

    private ServerSocketChannel serverSocketChannel;
    private Selector OP_ACCEPT;
    private Selector OP_READ;

    private AbstractExtension abstractExtension;
    private Configuration configuration;
    private Database database;
    private NioConnectionListener nioConnectionListener;
    private NioConnectionReader nioConnectionReader;
    private Monitor monitorServer;

    public void start() {
        this.configuration = new Configuration();
        if (this.configuration.init()) {
            this.startTimeMillis = System.currentTimeMillis();
            this.database = new Database(this.configuration);
            this.nioConnectionListener = new NioConnectionListener();
            this.nioConnectionReader = new NioConnectionReader();
            this.monitorServer = new Monitor();
            try {
                LOGGER.info("Max Message Length: " + Configuration.c_maxMsgLen);
                LOGGER.info("Max Connections: " + Configuration.c_maxConnections);
                LOGGER.info("Server Port: " + Configuration.s_port);
                LOGGER.info("Database Driver: " + Configuration.db_driver);
                LOGGER.info("Extension: " + Configuration.s_extension);
                if (this.bind()) {
                    this.nioConnectionListener.bind(this.serverSocketChannel, this.OP_ACCEPT, this.OP_READ);
                    this.nioConnectionReader.bind(this.OP_READ);
                    this.abstractExtension = (AbstractExtension) Class.forName(Configuration.s_extension).newInstance();
                    LOGGER.info("Server started in: " + (System.currentTimeMillis() - this.startTimeMillis) + "ms.");
                    TASK.schedule(this.nioConnectionListener, 3L, TimeUnit.NANOSECONDS);
                    TASK.schedule(this.nioConnectionReader, 3L, TimeUnit.NANOSECONDS);
                    TASK.schedule(this.monitorServer, 3L, TimeUnit.NANOSECONDS);
                    TASK.schedule(()-> this.abstractExtension.init(), 250L, TimeUnit.NANOSECONDS);
                } else {
                    LOGGER.info("Can't start nio.");
                }
            } catch (ClassNotFoundException e) {
                LOGGER.info("Class not found: " + Configuration.s_extension);
            } catch (IllegalAccessException e) {
                LOGGER.info("Illegal access: " + Configuration.s_extension);
            } catch (InstantiationException e) {
                LOGGER.info("Illegal access: " + Configuration.s_extension);
            }
        } else {
            LOGGER.info("Can't load configuration.");
        }
    }

    public boolean bind() {
        try {
            this.clients = new HashMap();
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(Configuration.s_port));
            this.serverSocketChannel.configureBlocking(Configuration.s_blocking);
            this.OP_ACCEPT = Selector.open();
            this.OP_READ = Selector.open();
            this.serverSocketChannel.register(this.OP_ACCEPT, SelectionKey.OP_ACCEPT);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public AbstractExtension getAbstractExtension() {
        return this.abstractExtension;
    }

    public static FateEmulator getInstance() {
        return instance;
    }

    public int getClientCount() {
        return this.clients.size();
    }

    public void addClient(SocketChannel socketChannel, NioConnectionHandler nioConnectionListener) {
        synchronized (this.clients) {
            this.clients.put(socketChannel, nioConnectionListener);
            if (Configuration.s_debug) {
                LOGGER.info("New client: " + nioConnectionListener.getIpAddress());
            }
        }
    }

    public void removeClient(SocketChannel socketChannel, NioConnectionHandler nioConnectionListener) {
        synchronized (this.clients) {
            this.clients.remove(socketChannel);
            if (Configuration.s_debug) {
                LOGGER.info("Removed client: " + nioConnectionListener.getIpAddress());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure("config/log4j.properties");
        System.out.println("|:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|");
        System.out.println("|                         Fate Emulator                         |");
        System.out.println("|              Massively Multiplayer Online Server              |");
        System.out.println("|                   (c) 2017 - Thalys Márcio                    |");
        System.out.println("|:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::|");
        instance = new FateEmulator();
        instance.start();
    }
}
