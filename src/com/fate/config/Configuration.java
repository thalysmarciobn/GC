package com.fate.config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;

public class Configuration {

    public static String db_address;
    public static int db_port;
    public static int db_maxConnectionsPool;
    public static int db_connectionTimeout;
    public static int db_validationTimeout;
    public static int db_leakDetectionThreshold;
    public static int db_maxLifetime;
    public static int db_idleTimeout;
    public static boolean db_autoCommit;
    public static String db_username;
    public static String db_password;
    public static String db_database;
    public static String db_driver;

    public static int c_maxConnections;
    public static int c_maxMsgLen;
    public static int c_elapsedTime;

    public static boolean s_debug;
    public static String s_address;
    public static int s_port;
    public static boolean s_blocking;
    public static String s_extension;

    public static String ws_address;
    public static int ws_port;

    public boolean init() {
        try {
            IXMLParser ixmlParser = XMLParserFactory.createDefaultXMLParser();
            IXMLReader ixmlReader = StdXMLReader.stringReader(readFile("config/fate.xml", Charset.defaultCharset()));
            ixmlParser.setReader(ixmlReader);
            IXMLElement ixmlElement = (IXMLElement) ixmlParser.parse();
            IXMLElement server = ixmlElement.getFirstChildNamed("server");
            if (server != null) {
                this.s_debug = Boolean.valueOf(server.getFirstChildNamed("debug").getContent());
                this.s_address = server.getFirstChildNamed("serverIP").getContent();
                this.s_port = Integer.valueOf(server.getFirstChildNamed("serverPort").getContent());
                this.s_blocking = Boolean.valueOf(server.getFirstChildNamed("serverPort").getContent());
                this.s_extension = server.getFirstChildNamed("extension").getContent();
                IXMLElement websocket = server.getFirstChildNamed("websocket");
                if (websocket != null) {
                    this.ws_address = websocket.getFirstChildNamed("address").getContent();
                    this.ws_port = Integer.valueOf(websocket.getFirstChildNamed("port").getContent());
                }
                IXMLElement database = server.getFirstChildNamed("database");
                if (database != null) {
                    this.db_address = database.getFirstChildNamed("address").getContent();
                    this.db_port = Integer.valueOf(database.getFirstChildNamed("port").getContent());
                    this.db_maxConnectionsPool = Integer.valueOf(database.getFirstChildNamed("maxConnections").getContent());
                    this.db_connectionTimeout = Integer.valueOf(database.getFirstChildNamed("connectionTimeout").getContent());
                    this.db_validationTimeout = Integer.valueOf(database.getFirstChildNamed("validationTimeout").getContent());
                    this.db_leakDetectionThreshold = Integer.valueOf(database.getFirstChildNamed("leakDetectionThreshold").getContent());
                    this.db_maxLifetime = Integer.valueOf(database.getFirstChildNamed("maxLifetime").getContent());
                    this.db_idleTimeout = Integer.valueOf(database.getFirstChildNamed("idleTimeout").getContent());
                    this.db_username = database.getFirstChildNamed("username").getContent();
                    this.db_password = database.getFirstChildNamed("password").getContent();
                    this.db_database = database.getFirstChildNamed("database").getContent();
                    this.db_driver = database.getFirstChildNamed("driver").getContent();
                    this.db_autoCommit = Boolean.valueOf(database.getFirstChildNamed("driver").getContent());
                }
                IXMLElement channel = server.getFirstChildNamed("channel");
                if (channel != null) {
                    this.c_maxConnections = Integer.valueOf(channel.getFirstChildNamed("maxConnections").getContent());
                    this.c_maxMsgLen = Integer.valueOf(channel.getFirstChildNamed("maxMsgLen").getContent());
                    this.c_elapsedTime = Integer.valueOf(channel.getFirstChildNamed("elapsedTime").getContent());
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String readFile(String path, Charset encoding) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), encoding);
    }
}
