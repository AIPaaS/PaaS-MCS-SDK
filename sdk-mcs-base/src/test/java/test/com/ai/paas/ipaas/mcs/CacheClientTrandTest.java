package test.com.ai.paas.ipaas.mcs;

import static org.junit.Assert.*;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ai.paas.ipaas.mcs.impl.CacheClient;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class CacheClientTrandTest {

	private static ICacheClient client = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		String host = "10.15.16.130:8801";
		client = new CacheClient(config, host, "asc123");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStartTransaction() throws Exception {
		for (int i = 0; i <= 100; i++) {
			client.set("dxf", "123456");
			Transaction tx = client.startTransaction();
			tx.set("dxf", "123");
			tx.incrBy("dxf", 123L);
			client.commitTransaction(tx);
		}
	}

}
