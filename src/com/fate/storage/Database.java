package com.fate.storage;

import com.fate.config.Configuration;

import java.util.HashMap;

public class Database {
    private static Database instance;
    private HashMap<String, Pool> pools;
    private Configuration configuration;

    public Database(Configuration configuration) {
        this.instance = this;
        this.configuration = configuration;
        this.pools = new HashMap();
    }

    public static Database getInstance() {
        return instance;
    }

    public Pool create(String poolName) {
        Pool pool = new Pool(poolName, this.configuration);
        this.pools.put(poolName, pool);
        return pool;
    }
    public Pool get(String poolName) {
        return this.pools.get(poolName);
    }
}
