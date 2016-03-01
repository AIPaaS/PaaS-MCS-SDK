package com.ai.paas.ipaas.mcs;

import com.ai.paas.ipaas.mcs.exception.CacheClientException;
import com.ai.paas.ipaas.mcs.impl.CacheClient;
import com.ai.paas.ipaas.mcs.impl.CacheClusterClient;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class CacheFactory {

    private static Map<String, ICacheClient> cacheClients = new ConcurrentHashMap<String, ICacheClient>();

    private CacheFactory() {
        // do nothing
    }

    public static ICacheClient getClient(Properties config) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(Integer.parseInt(config.getProperty("mcs.maxtotal", "20")));
        genericObjectPoolConfig.setMaxIdle(Integer.parseInt(config.getProperty("mcs.maxIdle", "10")));
        genericObjectPoolConfig.setMinIdle(Integer.parseInt(config.getProperty("mcs.minIdle", "5")));
        genericObjectPoolConfig.setTestOnBorrow(Boolean.parseBoolean(config.getProperty("mcs.testOnBorrow", "true")));

        String host = config.getProperty("mcs.host", "127.0.0.1:6379");
        String password = config.getProperty("mcs.password");
        if (password == null || password.length() == 0) {
            throw new CacheClientException("mcs.password cannot be null");
        }

        String[] hostArray = host.split(";");
        ICacheClient cacheClient = null;
        if (hostArray.length > 1) {
            cacheClient = new CacheClusterClient(genericObjectPoolConfig, hostArray, password);
        } else {
            cacheClient = new CacheClient(genericObjectPoolConfig, host, password);
        }
        cacheClients.put(host, cacheClient);
        return cacheClient;
    }

    public static ICacheClient getClient() throws IOException {
        Properties config = new Properties();
        config.load(CacheFactory.class.getResourceAsStream("/msc.conf"));
        return getClient(config);
    }

    public static ICacheClient getClient(InputStream inputStream) throws IOException {
        Properties config = new Properties();
        config.load(inputStream);
        return getClient(config);
    }
}
