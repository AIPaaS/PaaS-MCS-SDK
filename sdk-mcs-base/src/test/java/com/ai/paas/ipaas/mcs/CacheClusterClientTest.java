package com.ai.paas.ipaas.mcs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ai.paas.ipaas.mcs.exception.CacheException;
import com.ai.paas.ipaas.mcs.impl.CacheClusterClient;


public class CacheClusterClientTest {
    private static ICacheClient client = null;

    @SuppressWarnings("rawtypes")
    @BeforeClass
    public static void setUpBeforeClass() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        String host = "10.1.235.122:31001,10.1.235.123:31001,10.1.235.124:31001,10.1.235.122:31000,10.1.235.123:31000,10.1.235.124:31000";
        String[] hosts = host.split(",");
        client = new CacheClusterClient(config, hosts, "QAZ234WSx");
    }

    @Test
    public void testSetStringString() {
        client.set("dxf", "123456");
        client.set("123456", "dxf");
        assertTrue("123456".equals(client.get("dxf")));
        assertTrue("dxf".equals(client.get("123456")));
    }

    @Test
    public void testSetexStringIntString() throws InterruptedException {
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
        client.set("123", "123456");
        client.del("123");
        assertNotEquals("123456", client.get("123"));
    }

    @Test(expected=CacheException.class)
    public void testDelStringArray() {
        client.set("123", "123456");
        client.set("dxf", "123456");
        String[] keys = { "123", "dxf" };
        client.del(keys);
        assertNotEquals("123456", client.get("123"));
        assertNotEquals("123456", client.get("dxf"));
    }

    @Test
    public void testExpireStringInt() throws InterruptedException {
        client.set("dxf", "123456");
        Date d = Calendar.getInstance(Locale.CHINA).getTime();
        client.expireAt("dxf", d.getTime() / 1000 + 10);
        d.setTime(1524395546879L);
        Thread.sleep(12000);
        assertNotEquals("123456", client.get("dxf"));
    }

    @Test
    public void testExpireAtStringLong() throws InterruptedException {
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
        client.del("firset");
        Map<String, String> map = new HashMap<>();
        map.put("second", "123456");
        map.put("third", "12345678");
        client.hmset("firset", map);
        assertTrue("12345678".equals(client.hget("firset", "third")));
        client.del("firset");
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

    @Test(expected = CacheException.class)
    public void testSunionStringArray() {
        // 由于跨槽位不允许操作，可能会报异常
        String[] members = { "one", "two", "three", "three" };
        client.sadd("set", members);
        String[] members1 = { "four", "five", "three", "six" };
        client.sadd("set1", members1);
        Set<String> sets = client.sunion("set", "set1");
        assertTrue(6 == sets.size());
        client.del("set");
        client.del("set1");
    }

    @Test(expected=CacheException.class)
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

    @Test(expected = CacheException.class)
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
        client.del("one".getBytes());
        client.del("two".getBytes());
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
        client.rpush("one111".getBytes(), "two".getBytes(), "three".getBytes());
        List<byte[]> values = client.lrangeAll("one111".getBytes());
        assertTrue("three".equals(new String(values.get(1))));
        client.del("one111");
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
        client.set("dxf", "123456");
        client.setnx("dxf".getBytes(), "12345678".getBytes());
        assertTrue("123456".equals(client.get("dxf")));
        client.del("dxf");
        client.setnx("dxf".getBytes(), "12345678".getBytes());
        assertTrue("12345678".equals(client.get("dxf")));
        client.del("dxf");
    }

    @Test
    public void testSetnxStringString() {
        client.set("dxf", "123456");
        client.setnx("dxf", "12345678");
        assertTrue("123456".equals(client.get("dxf")));
        client.del("dxf");
        client.setnx("dxf", "12345678");
        assertTrue("12345678".equals(client.get("dxf")));
        client.del("dxf");
    }

    @Test
    public void testHmsetByteArrayMapOfbytebyte() {
        Map<byte[], byte[]> map = new HashMap<>();
        map.put("one".getBytes(), "123456".getBytes());
        map.put("two".getBytes(), "123".getBytes());
        client.hmset("dxf".getBytes(), map);
        assertTrue("123456".equals(client.hmget("dxf", "one").get(0)));
        client.del("dxf");
    }

    @Test
    public void testHgetByteArrayByteArray() {
        Map<String, String> map = new HashMap<>();
        map.put("one", "123456");
        map.put("two", "123");
        client.hmset("dxf123456", map);
        assertTrue("123456".equals(client.hmget("dxf123456", "one").get(0)));
        client.del("dxf123456");
    }

    @Test
    public void testHmgetByteArrayByteArrayArray() {
        Map<byte[], byte[]> map = new HashMap<>();
        map.put("one".getBytes(), "123456".getBytes());
        map.put("two".getBytes(), "123".getBytes());
        client.hmset("dxf1234567".getBytes(), map);
        assertTrue("123456".equals(new String(client.hmget("dxf1234567".getBytes(), "one".getBytes()).get(0))));
        client.del("dxf1234567");
    }

    @Test
    public void testHexistsByteArrayByteArray() {
        Map<byte[], byte[]> map = new HashMap<>();
        map.put("one".getBytes(), "123456".getBytes());
        map.put("two".getBytes(), "123".getBytes());
        client.hmset("dxf".getBytes(), map);
        assertTrue(client.hexists("dxf".getBytes(), "two".getBytes()));
        client.del("dxf");
    }

    @Test
    public void testHdelByteArrayByteArrayArray() {
        Map<byte[], byte[]> map = new HashMap<>();
        map.put("one".getBytes(), "123456".getBytes());
        map.put("two".getBytes(), "123".getBytes());
        client.hmset("dxfmap".getBytes(), map);
        client.hdel("dxfmap".getBytes(), "one".getBytes());
        assertTrue(!client.hexists("dxfmap".getBytes(), "one".getBytes()));
        client.del("dxfmap");
    }

    @Test
    public void testHlenByteArray() {
        Map<byte[], byte[]> map = new HashMap<>();
        map.put("one".getBytes(), "123456".getBytes());
        map.put("two".getBytes(), "123".getBytes());
        client.hmset("dxfmaplen".getBytes(), map);
        Long count = client.hlen("dxfmaplen".getBytes());
        assertTrue(2 == count);
        client.del("dxfmaplen");
    }

    @Test
    public void testHgetAllByteArray() {
        Map<byte[], byte[]> map = new HashMap<>();
        map.put("one".getBytes(), "123456".getBytes());
        map.put("two".getBytes(), "123".getBytes());
        client.hmset("dxf1231".getBytes(), map);
        map = client.hgetAll("dxf1231".getBytes());
        assertTrue(2 == map.size());
        client.del("dxf1231");
    }

    @Test
    public void testSaddByteArrayByteArrayArray() {
        byte[] members1 = "123456".getBytes();
        byte[] members2 = "123".getBytes();
        client.sadd("dxf1234".getBytes(), members1, members2);
        assertTrue(2 == client.smembers("dxf1234".getBytes()).size());
        client.del("dxf1234");
    }

    @Test
    public void testSmembersByteArray() {
        byte[] members1 = "123456".getBytes();
        byte[] members2 = "123".getBytes();
        client.sadd("dxf".getBytes(), members1, members2);
        Set<byte[]> sets = client.smembers("dxf".getBytes());
        assertTrue(2 == sets.size());
        client.del("dxf");
    }

    @Test
    public void testSremByteArrayByteArrayArray() {
        byte[] members1 = "123456".getBytes();
        byte[] members2 = "123".getBytes();
        client.sadd("dxf".getBytes(), members1, members2);
        client.srem("dxf".getBytes(), members1);
        assertTrue(1 == client.smembers("dxf".getBytes()).size());
        client.del("dxf");
    }

    @Test
    public void testScardByteArray() {
        byte[] members1 = "123456".getBytes();
        byte[] members2 = "123".getBytes();
        client.sadd("dxf111".getBytes(), members1, members2);
        Long count = client.scard("dxf111".getBytes());
        assertTrue(2 == count);
        client.del("dxf111");
    }

    @Test(expected = CacheException.class)
    public void testSunionByteArrayArray() {
        byte[] members1 = "123".getBytes();
        byte[] members2 = "1234".getBytes();
        client.sadd("dxf".getBytes(), members1, members2);
        byte[] members3 = "12345".getBytes();
        byte[] members4 = "123456".getBytes();
        client.del("dxf1");
        client.sadd("dxf1".getBytes(), members3, members4);
        Set<byte[]> sets = client.sunion("dxf1".getBytes(), "dxf".getBytes());
        assertTrue(4 == sets.size());
        client.del("dxf");
        client.del("dxf1");
    }

    @Test(expected = CacheException.class)
    public void testSdiffByteArrayArray() {
        // 跨槽位的集合处理不能做，抛异常
        byte[] members1 = "123".getBytes();
        byte[] members2 = "1234".getBytes();
        client.sadd("dxf".getBytes(), members1, members2);
        byte[] members3 = "123".getBytes();
        byte[] members4 = "123456".getBytes();
        client.del("dxf1");
        client.sadd("dxf1".getBytes(), members3, members4);
        Set<byte[]> sets = client.sdiff("dxf".getBytes(), "dxf1".getBytes());
        assertTrue(1 == sets.size());
        client.del("dxf");
        client.del("dxf1");
    }

    @Test(expected = CacheException.class)
    public void testSdiffstoreByteArrayByteArrayArray() {
        byte[] members1 = "123".getBytes();
        byte[] members2 = "1234".getBytes();
        client.sadd("dxf".getBytes(), members1, members2);
        byte[] members3 = "123".getBytes();
        byte[] members4 = "123456".getBytes();
        client.del("dxf1");
        client.sadd("dxf1".getBytes(), members3, members4);
        Long count = client.sdiffstore("sets".getBytes(), "dxf".getBytes(), "dxf1".getBytes());
        assertTrue(1 == count);
        client.del("dxf");
        client.del("dxf1");
    }

    @Test
    public void testHincrBy() {
        client.del("dxf");
        client.hset("dxf", "key1", "123");
        long value = client.hincrBy("dxf", "key1", 1);
        assertTrue(124 == value);
        client.del("dxf");
    }

    @Test
    public void testIncrByFloat() {
        client.del("dxf");
        client.set("dxf", "123");
        double value = client.incrByFloat("dxf", 1.1D);
        assertTrue(124.1 == value);
        client.del("dxf");
    }

    @Test
    public void testHincrByFloat() {
        client.del("dxf");
        client.hset("dxf", "key1", "123");
        double value = client.hincrByFloat("dxf", "key1", 1.1D);
        assertTrue(124.1 == value);
        client.del("dxf");
    }

}
