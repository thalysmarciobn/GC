package com.fate.storage;

import com.fate.config.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jdbchelper.JdbcHelper;

public class Pool {

    private Configuration configuration;
    private HikariConfig hikariConfig;
    private HikariDataSource hikariDataSource;
    private JdbcHelper jdbcHelper;

    public Pool(String poolName, Configuration configuration) {
        this.configuration = configuration;
        this.hikariConfig = new HikariConfig();
        this.hikariConfig.setJdbcUrl("jdbc:mysql://" + this.configuration.getDatabase().getAddress() + ":" + this.configuration.getDatabase().getPort() + "/" + this.configuration.getDatabase().getDatabase() + "?tcpKeepAlive=true&autoReconnect=true");
        this.hikariConfig.setDriverClassName(this.configuration.getDatabase().getDriver());
        this.hikariConfig.setPoolName(poolName);
        this.hikariConfig.setUsername(this.configuration.getDatabase().getUsername());
        this.hikariConfig.setPassword(this.configuration.getDatabase().getPassword());
        this.hikariConfig.setMaximumPoolSize(this.configuration.getDatabase().getMaxConnections());
        this.hikariConfig.setAutoCommit(this.configuration.getDatabase().isAutoCommit());
        this.hikariConfig.setConnectionTimeout(this.configuration.getDatabase().getConnectionTimeout());
        this.hikariConfig.setValidationTimeout(this.configuration.getDatabase().getValidationTimeout());
        this.hikariConfig.setLeakDetectionThreshold(this.configuration.getDatabase().getLeakDetectionThreshold());
        this.hikariConfig.setMaxLifetime(this.configuration.getDatabase().getMaxLifetime());
        this.hikariConfig.setIdleTimeout(this.configuration.getDatabase().getIdleTimeout());
    }

    public boolean connect() {
        try {
            this.hikariDataSource = new HikariDataSource(this.hikariConfig);
            this.jdbcHelper = new JdbcHelper(this.hikariDataSource);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public HikariDataSource getHikariDataSource() {
        return this.hikariDataSource;
    }

    public JdbcHelper getJdbcHelper() {
        return this.jdbcHelper;
    }
}
