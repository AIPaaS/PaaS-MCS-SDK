package test.com.ai.paas.ipaas.mcs;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ai.paas.ipaas.mcs.impl.CacheClient;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

public class CacheClientTest {
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
	public void testKeys() {
		Set<String> keys = client.keys("dxf*");
		System.out.println(keys);
		assertEquals(1, keys.size());
	}

	@Test
	public void testSetStringString() {
		client.set("123", "123456");
		assertEquals("123456", client.get("123"));
		client.del("123");
	}

	@Test
	public void testSetexStringIntString() throws Exception {
		client.setex("dxf", 10, "123456");
		assertEquals("123456", client.get("dxf"));
		Thread.sleep(12000);
		assertNotEquals("123456", client.get("dxf"));
	}

	@Test
	public void testGetString() {
		client.set("123", "123456");
		assertEquals("123456", client.get("123"));
		client.del("123");
	}

	@Test
	public void testDelString() {
		// for(int i=0;i<100;i++){
		client.set("123", "123456");
		client.del("123");
		assertNotEquals("123456", client.get("123"));
		// }
	}

	@Test
	public void testDelStringArray() {
		client.set("123", "123456");
		client.set("dxf", "123456");
		String[] keys = { "123", "dxf" };
		client.del(keys);
		assertNotEquals("123456", client.get("123"));
		assertNotEquals("123456", client.get("dxf"));
	}

	@Test
	public void testExpireStringInt() throws Exception {
		client.set("dxf", "123456");
		Date d = Calendar.getInstance(Locale.CHINA).getTime();
		System.out.println(d.getTime());
		client.expireAt("dxf", d.getTime() / 1000 + 10);
		System.out.println(client.ttl("dxf"));
		System.out.println(d);
		d.setTime(1524395546879L);
		System.out.println(d);
		Thread.sleep(12000);
		assertNotEquals("123456", client.get("dxf"));
	}

	@Test
	public void testExpireAtStringLong() throws Exception {
		client.set("dxf", "123456");
		client.expire("dxf", 10);
		Thread.sleep(12000);
		assertNotEquals("123456", client.get("dxf"));
	}

	@Test
	public void testTtlString() throws Exception {
		client.set("dxf", "123456");
		client.expire("dxf", 10);
		Thread.sleep(1000);
		System.out.println(client.ttl("dxf"));
		assertTrue(10 > client.ttl("dxf"));
	}

	@Test
	public void testExistsString() {
		client.set("dxf123", "123456");
		assertTrue(client.exists("dxf123"));
	}

	@Test
	public void testIncrString() {
		client.set("123", "1");
		client.incr("123");
		assertTrue("2".equals(client.get("123")));
	}

	@Test
	public void testIncrByStringLong() {
		client.set("123", "1");
		client.incrBy("123", 100L);
		assertTrue("101".equals(client.get("123")));
	}

	@Test
	public void testDecrString() {
		client.set("123", "1");
		client.decr("123");
		assertTrue("0".equals(client.get("123")));
	}

	@Test
	public void testDecrByStringLong() {
		client.set("123", "100");
		client.decrBy("123", 99);
		assertTrue("1".equals(client.get("123")));
	}

	@Test
	public void testLpushStringStringArray() {
		String[] values = { "A", "B", "C" };
		client.lpush("push", values);
		assertTrue("C".equals(client.lpop("push")));
		client.del("push");
	}

	@Test
	public void testRpushStringStringArray() {
		String[] values = { "A", "B", "C" };
		client.rpush("push", values);
		assertTrue("A".equals(client.lpop("push")));
		client.del("push");
	}

	@Test
	public void testLremStringLongString() {
		String[] values = { "A", "B", "C", "A", "B", "C" };
		client.rpush("push", values);
		client.lrem("push", 2, "A");
		assertTrue("B".equals(client.lpop("push")));
		client.del("push");
	}

	@Test
	public void testLlenString() {
		String[] values = { "A", "B", "C", "A", "B", "C" };
		client.rpush("push", values);
		assertTrue(6 == client.llen("push"));
		client.del("push");
	}

	@Test
	public void testLpopString() {
		String[] values = { "A", "B", "C", "A", "B", "C" };
		client.rpush("push", values);
		assertTrue("A".equals(client.lpop("push")));
		client.del("push");
	}

	@Test
	public void testRpopString() {
		String[] values = { "A", "B", "C", "A", "B", "C" };
		client.rpush("push", values);
		assertTrue("C".equals(client.rpop("push")));
		client.del("push");
	}

	@Test
	public void testLrangeStringLongLong() {
		String[] values = { "A", "B", "C", "A", "B", "C" };
		client.rpush("push", values);
		List<String> list = client.lrange("push", 0, 2);
		assertTrue(3 == list.size());
		client.del("push");
	}

	@Test
	public void testLrangeAllString() {
		String[] values = { "A", "B", "C", "A", "B", "C" };
		client.rpush("push", values);
		List<String> list = client.lrangeAll("push");
		assertTrue(6 == list.size());
		client.del("push");
	}

	@Test
	public void testHsetStringStringString() {
		client.hset("first", "second", "123456");
		assertTrue("123456".equals(client.hget("first", "second")));
		client.del("first");
	}

	@Test
	public void testHsetnxStringStringString() {
		client.hset("first", "second", "123456");
		client.hsetnx("first", "second", "12345678");
		assertTrue("123456".equals(client.hget("first", "second")));
		client.del("first");
		client.hsetnx("first", "second", "12345678");
		assertTrue("12345678".equals(client.hget("first", "second")));
		client.del("first");
	}

	@Test
	public void testHmsetStringMapOfStringString() {
		Map<String, String> map = new HashMap<>();
		map.put("second", "123456");
		map.put("third", "12345678");
		client.hmset("firset", map);
		assertTrue("12345678".equals(client.hget("first", "third")));
	}

	@Test
	public void testHgetStringString() {
		client.hset("first", "second", "123456");
		assertTrue("123456".equals(client.hget("first", "second")));
		client.del("first");
	}

	@Test
	public void testHmgetStringStringArray() {
		Map<String, String> map = new HashMap<>();
		map.put("second", "123456");
		map.put("third", "12345678");
		client.hmset("firset", map);
		map = null;
		String[] fields = { "third" };
		List<String> list = client.hmget("firset", fields);
		assertTrue("12345678".equals(list.get(0)));
		client.del("firset");
	}

	@Test
	public void testHexistsStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testHdelStringStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testHlenString() {
		fail("Not yet implemented");
	}

	@Test
	public void testHgetAllString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaddStringStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSmembersString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSremStringStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testScardString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSunionStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSdiffStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSdiffstoreStringStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetByteArrayByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetexByteArrayIntByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelByteArrayArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testExpireByteArrayInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testExpireAtByteArrayLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testTtlByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testExistsByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testIncrByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testIncrByByteArrayLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecrByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecrByByteArrayLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testLpushByteArrayByteArrayArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testRpushByteArrayByteArrayArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testLlenByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testLremByteArrayLongByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testLpopByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testRpopByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testLrangeByteArrayLongLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testLrangeAllByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testHsetByteArrayByteArrayByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testHsetnxByteArrayByteArrayByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetnxByteArrayByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetnxStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testHmsetByteArrayMapOfbytebyte() {
		fail("Not yet implemented");
	}

	@Test
	public void testHgetByteArrayByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testHmgetByteArrayByteArrayArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testHexistsByteArrayByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testHdelByteArrayByteArrayArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testHlenByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testHgetAllByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaddByteArrayByteArrayArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSmembersByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSremByteArrayByteArrayArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testScardByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSunionByteArrayArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSdiffByteArrayArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSdiffstoreByteArrayByteArrayArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testHincrBy() {
		fail("Not yet implemented");
	}

	@Test
	public void testIncrByFloat() {
		fail("Not yet implemented");
	}

	@Test
	public void testHincrByFloat() {
		fail("Not yet implemented");
	}

}
