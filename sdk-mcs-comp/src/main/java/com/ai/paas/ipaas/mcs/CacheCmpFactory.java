package com.ai.paas.ipaas.mcs;

import com.ai.paas.ipaas.mcs.impl.CacheClient;
import com.ai.paas.ipaas.mcs.impl.CacheClusterClient;
import com.ai.paas.ipaas.mcs.impl.CacheCodisClient;
import com.ai.paas.ipaas.mcs.impl.CacheSentinelClient;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.util.Assert;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class CacheCmpFactory {

	private static Map<String, ICacheClient> cacheClients = new ConcurrentHashMap<String, ICacheClient>();
	private final static String MCS_MODE_SINGILE = "single";
	private final static String MCS_MODE_MASTER = "master";
	private final static String MCS_MODE_SENTINEL = "sentinel";
	private final static String MCS_MODE_CODIS = "codis";
	private final static String MCS_MODE_CLUSTER = "cluster";

	private CacheCmpFactory() {
		// do nothing
	}

	/**
	 * 
	 * @param config
	 *            "mcs.mode":"single|master|sentinel|cluster|codis","mcs.
	 *            maxtotal", "500","mcs.maxIdle", "10","mcs.minIdle", "5",
	 *            "mcs.testOnBorrow",
	 *            "true","mcs.codis.zk.addr":"xxx.xxx.xxx.xxx:2181;xxx.xxx.xxx.
	 *            xxx:2181;xxx.xxx.xxx.xxx:2181","mcs.codis.zk.path", ""
	 * @return
	 */
	public static ICacheClient getClient(Properties config) {
		GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
		genericObjectPoolConfig.setMaxTotal(Integer.parseInt(config.getProperty("mcs.maxtotal", "500")));
		genericObjectPoolConfig.setMaxIdle(Integer.parseInt(config.getProperty("mcs.maxIdle", "10")));
		genericObjectPoolConfig.setMinIdle(Integer.parseInt(config.getProperty("mcs.minIdle", "5")));
		genericObjectPoolConfig.setTestOnBorrow(Boolean.parseBoolean(config.getProperty("mcs.testOnBorrow", "true")));

		String mode = config.getProperty("mcs.mode", "single");
		String host = config.getProperty("mcs.host", "127.0.0.1:6379");
		String password = config.getProperty("mcs.password", "");
		Assert.notNull(mode, "mcs.mode can not be null, must be one of single|master|sentinel|cluster|codis");
		Assert.notNull(host, "redis host address can not be null. mcs.host must have values");
		String key = mode.hashCode() + "-" + host.hashCode();

		ICacheClient cacheClient = null;
		if (null != cacheClients.get(host)) {
			return cacheClients.get(host);
		}
		// add codis support
		switch (mode) {
		case MCS_MODE_SINGILE:
			cacheClient = new CacheClient(genericObjectPoolConfig, host, password);
			break;
		case MCS_MODE_MASTER:
			cacheClient = new CacheClient(genericObjectPoolConfig, host, password);
			break;
		case MCS_MODE_SENTINEL:
			cacheClient = new CacheSentinelClient(genericObjectPoolConfig, host, password);
			break;
		case MCS_MODE_CLUSTER:
			String[] hostArray = host.split(";|,");
			cacheClient = new CacheClusterClient(genericObjectPoolConfig, hostArray, password);
			break;
		case MCS_MODE_CODIS:
			String zkAddr = config.getProperty("mcs.codis.zk.addr", "");
			String zkPath = config.getProperty("mcs.codis.zk.path", "");
			Assert.notNull(zkAddr, "Must set mcs.codis.zk.addr when use coide mode!");
			Assert.notNull(zkAddr, "Must set mcs.codis.zk.path(/zk/codis/db_xxx/proxy|/jodis/xxx) when use coide mode!");
			cacheClient = new CacheCodisClient(zkAddr, zkPath);
			break;
		default:
			cacheClient = new CacheClient(genericObjectPoolConfig, host, password);
		}

		cacheClients.put(key, cacheClient);
		return cacheClient;
	}

	public static ICacheClient getClient() throws IOException {
		Properties config = new Properties();
		config.load(CacheCmpFactory.class.getResourceAsStream("/redis.conf"));
		return getClient(config);
	}

	public static ICacheClient getClient(InputStream inputStream) throws IOException {
		Properties config = new Properties();
		config.load(inputStream);
		return getClient(config);
	}
}
