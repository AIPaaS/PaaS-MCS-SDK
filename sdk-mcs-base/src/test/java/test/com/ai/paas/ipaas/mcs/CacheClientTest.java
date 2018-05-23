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
		String host = "10.15.16.130:9801";
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
		for(int i=1;i<100000;i++){
			client.set("dxf"+i, "1234567"+i);
			client.get("dxf"+i);
		}
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
		Map<String, String> map = new HashMap<>();
		map.put("second", "123456");
		map.put("third", "12345678");
		client.hmset("firset", map);
		assertTrue(client.hexists("firset", "second"));
		client.del("firset");
	}

	@Test
	public void testHdelStringStringArray() {
		Map<String, String> map = new HashMap<>();
		map.put("second", "123456");
		map.put("third", "12345678");
		client.hmset("firset", map);
		client.hdel("firset", "third");
		assertTrue(!client.hexists("firset", "third"));
		client.del("firset");
	}

	@Test
	public void testHlenString() {
		Map<String, String> map = new HashMap<>();
		map.put("second", "123456");
		map.put("third", "12345678");
		client.hmset("firset", map);
		assertTrue(2 == client.hlen("firset"));
		client.del("firset");
	}

	@Test
	public void testHgetAllString() {
		Map<String, String> map = new HashMap<>();
		map.put("second", "123456");
		map.put("third", "12345678");
		client.hmset("firset", map);
		map = client.hgetAll("firset");
		assertTrue(2 == map.size());
		client.del("firset");
	}

	@Test
	public void testSaddStringStringArray() {
		String[] members = { "one", "two", "three" };
		client.sadd("set", members);
		assertTrue(3 == client.scard("set"));
		client.del("set");
	}

	@Test
	public void testSmembersString() {
		String[] members = { "one", "two", "three", "three" };
		client.sadd("set", members);
		Set<String> sets = client.smembers("set");
		assertTrue(3 == sets.size());
		client.del("set");
	}

	@Test
	public void testSremStringStringArray() {
		String[] members = { "one", "two", "three", "three" };
		client.sadd("set", members);
		String[] rems = { "one", "four" };
		long cont = client.srem("set", rems);
		assertTrue(cont == 1);
		client.del("set");
	}

	@Test
	public void testScardString() {
		String[] members = { "one", "two", "three", "three" };
		client.sadd("set", members);
		assertTrue(3 == client.scard("set"));
		client.del("set");
	}

	@Test
	public void testSunionStringArray() {
		String[] members = { "one", "two", "three", "three" };
		client.sadd("set", members);
		String[] members1 = { "four", "five", "three", "six" };
		client.sadd("set1", members1);
		Set<String> sets = client.sunion("set", "set1");
		assertTrue(6 == sets.size());
		client.del("set");
		client.del("set1");
	}

	@Test
	public void testSdiffStringArray() {
		String[] members = { "one", "two", "three", "three" };
		client.sadd("set", members);
		String[] members1 = { "four", "five", "three", "six" };
		client.sadd("set1", members1);
		Set<String> sets = client.sdiff("set", "set1");
		assertTrue(2 == sets.size());
		client.del("set");
		client.del("set1");
	}

	@Test
	public void testSdiffstoreStringStringArray() {
		String[] members = { "one", "two", "three", "three" };
		client.sadd("set", members);
		String[] members1 = { "four", "five", "three", "six" };
		client.sadd("set1", members1);
		long count = client.sdiffstore("diff", "set", "set1");
		assertTrue(2 == count);
		client.del("set");
		client.del("set1");
		client.del("diff");
	}

	@Test
	public void testSetByteArrayByteArray() {
		client.set("one".getBytes(), "12345678".getBytes());
		assertTrue("12345678".equals(client.get("one")));
		client.del("one");
	}

	@Test
	public void testSetexByteArrayIntByteArray() throws Exception {
		client.set("one", "12345678");
		client.setex("one".getBytes(), 10, "123456".getBytes());
		assertTrue("123456".equals(client.get("one")));
		Thread.sleep(12000);
		assertNull(client.get("one"));
	}

	@Test
	public void testGetByteArray() {
		client.set("one", "12345678");
		byte[] value = client.get("one".getBytes());
		assertTrue("12345678".equals(new String(value)));
		client.del("one");
	}

	@Test
	public void testDelByteArray() {
		client.set("one", "12345678");
		client.del("one".getBytes());
		assertNull(client.get("one"));
	}

	@Test
	public void testDelByteArrayArray() {
		client.set("one", "123456");
		client.set("two", "789");
		client.del("one".getBytes(), "two".getBytes());
		assertNull(client.get("one"));
		assertNull(client.get("two"));
	}

	@Test
	public void testExpireByteArrayInt() throws Exception {
		client.set("one", "123456");
		client.expire("one".getBytes(), 5);
		Thread.sleep(6000);
		assertNull(client.get("one"));
	}

	@Test
	public void testExpireAtByteArrayLong() {
		client.set("one", "123456");
		Date d = new Date();
		client.expireAt("one", d.getTime() / 1000);
		assertNull(client.get("one"));
	}

	@Test
	public void testTtlByteArray() {
		client.setex("one", 10, "123456");
		long t = client.ttl("one".getBytes());
		assertTrue(t >= 0);
		client.del("one");
		client.set("one", "123456");
		t = client.ttl("one".getBytes());
		assertTrue(t == -1);
		client.del("one");
	}

	@Test
	public void testExistsByteArray() {
		client.set("one", "123456");
		assertTrue(client.exists("one".getBytes()));
		client.del("one");
	}

	@Test
	public void testIncrByteArray() {
		client.set("one", "123456");
		long t = client.incr("one".getBytes());
		assertTrue(t == 123457);
		client.del("one");
	}

	@Test
	public void testIncrByByteArrayLong() {
		client.set("one", "123456");
		long t = client.incrBy("one".getBytes(), 2);
		assertTrue(t == 123458);
		client.del("one");
	}

	@Test
	public void testDecrByteArray() {
		client.set("one", "123456");
		long t = client.decr("one".getBytes());
		assertTrue(t == 123455);
		client.del("one");
	}

	@Test
	public void testDecrByByteArrayLong() {
		client.set("one", "123456");
		long t = client.decrBy("one".getBytes(), 2);
		assertTrue(t == 123454);
		client.del("one");
	}

	@Test
	public void testLpushByteArrayByteArrayArray() {
		long count = client.lpush("one".getBytes(), "two".getBytes(), "three".getBytes());
		assertTrue(count == 2);
		assertTrue("three".equals(client.lpop("one")));
		client.del("one");

	}

	@Test
	public void testRpushByteArrayByteArrayArray() {
		long count = client.rpush("one".getBytes(), "two".getBytes(), "three".getBytes());
		assertTrue(count == 2);
		assertTrue("two".equals(client.lpop("one")));

		client.del("one");
	}

	@Test
	public void testLlenByteArray() {
		long count = client.rpush("one".getBytes(), "two".getBytes(), "three".getBytes());
		count = client.llen("one".getBytes());
		assertTrue(count == 2);
		client.del("one");
	}

	@Test
	public void testLremByteArrayLongByteArray() {
		client.rpush("one".getBytes(), "two".getBytes(), "three".getBytes());
		client.lrem("one".getBytes(), 1, "three".getBytes());
		assertTrue("two".equals(client.lpop("one")));
		client.del("one");
	}

	@Test
	public void testLpopByteArray() {
		client.rpush("one".getBytes(), "two".getBytes(), "three".getBytes());
		assertTrue("two".equals(new String(client.lpop("one".getBytes()))));
		client.del("one");
	}

	@Test
	public void testRpopByteArray() {
		client.rpush("one".getBytes(), "two".getBytes(), "three".getBytes());
		assertTrue("three".equals(new String(client.rpop("one".getBytes()))));
		client.del("one");
	}

	@Test
	public void testLrangeByteArrayLongLong() {
		client.rpush("one".getBytes(), "two".getBytes(), "three".getBytes());
		List<byte[]> values = client.lrange("one".getBytes(), 0, 1);
		assertTrue("two".equals(new String(values.get(0))));
		client.del("one");
	}

	@Test
	public void testLrangeAllByteArray() {
		client.rpush("one".getBytes(), "two".getBytes(), "three".getBytes());
		List<byte[]> values = client.lrangeAll("one".getBytes());
		assertTrue("three".equals(new String(values.get(1))));
		client.del("one");
	}

	@Test
	public void testHsetByteArrayByteArrayByteArray() {
		client.hset("map".getBytes(), "field".getBytes(), "value".getBytes());
		assertTrue("value".equals(client.hget("map", "field")));
		client.del("map");
	}

	@Test
	public void testHsetnxByteArrayByteArrayByteArray() {
		client.hset("map", "field", "value");
		client.hsetnx("map".getBytes(), "field".getBytes(), "value1".getBytes());
		assertTrue("value".equals(client.hget("map", "field")));
		client.del("map");
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
