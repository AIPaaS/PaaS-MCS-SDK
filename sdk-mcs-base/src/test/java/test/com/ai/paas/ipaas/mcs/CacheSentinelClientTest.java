package test.com.ai.paas.ipaas.mcs;

import static org.junit.Assert.*;

import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ai.paas.ipaas.mcs.impl.CacheClient;
import com.ai.paas.ipaas.mcs.impl.CacheSentinelClient;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

public class CacheSentinelClientTest {
	private static ICacheClient client = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		String host = "10.1.235.23:26379";
		client = new CacheSentinelClient(config, host, "asc123");
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
	public void testKeys() {
		Set<String> keys = client.keys("dxf*");
		System.out.println(keys);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
