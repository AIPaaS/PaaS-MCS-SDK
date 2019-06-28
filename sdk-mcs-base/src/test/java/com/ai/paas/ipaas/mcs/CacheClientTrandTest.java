package com.ai.paas.ipaas.mcs;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ai.paas.ipaas.mcs.impl.CacheClient;

import redis.clients.jedis.Transaction;

public class CacheClientTrandTest {

	private static ICacheClient client = null;

	@SuppressWarnings("rawtypes")
    @BeforeClass
	public static void setUpBeforeClass()  {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		String host = "10.15.16.130:9801";
		client = new CacheClient(config, host, "asc123");
	}


	@Test
	public void testStartTransaction() {
		for (int i = 0; i <= 100; i++) {
			client.set("dxf", "123456");
			Transaction tx = client.startTransaction();
			tx.set("dxf", "123");
			tx.incrBy("dxf", 123L);
			client.commitTransaction(tx);
		}
	}

}
