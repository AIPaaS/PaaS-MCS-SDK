package com.ai.paas.ipaas.mcs.impl;

import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class JedisContextHolder {
	private static transient final org.slf4j.Logger log = LoggerFactory.getLogger(CacheClient.class);
	private static final ThreadLocal<Jedis> txJedisHolder = new ThreadLocal<>();

	public static void clean() {
		log.info("Start to clean jedis....");
		if (null != txJedisHolder.get()) {
			txJedisHolder.get().close();
		}
		txJedisHolder.set(null);
	}

	public static void setJedis(Jedis jedis) {
		txJedisHolder.set(jedis);
	}

	public static Jedis getJedis() {
		return txJedisHolder.get();
	}
}
