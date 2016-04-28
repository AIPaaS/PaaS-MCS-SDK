package test.com.ai.paas.ipaas.mcs;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ai.paas.ipaas.mcs.CacheFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;

public class CacheTest {
	
	private static final String AUTH_ADDR = "http://10.1.228.198:14821/iPaas-Auth/service/auth";
//	private static final String AUTH_ADDR = "http://10.1.31.20:19821/iPaas-Auth/service/check";
		
	private static AuthDescriptor ad = null;
	private static ICacheClient ic = null;
	
	private static final String STR_KEY = "test";
	private static final byte[] BYTE_KEY = "testSet".getBytes();
	
	
	static{
		ad =  new AuthDescriptor(AUTH_ADDR, "FFF49D0D518948D0AB28D7A8EEE25D03", "Bjwqt7!@#","601");
		try {
			ic = CacheFactory.getClient(ad);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void seTest() {
		ic.set(STR_KEY, "1");
		ic.set(BYTE_KEY, "2".getBytes());
		
//		ic.setex("setex", 1000, "2");
//		ic.setex("seteX".getBytes(), 1000, "3".getBytes());
//		
//		ic.set("ts", "1");
//		ic.set("tss".getBytes(), "2".getBytes());
	}
	
	@Test
	public void getTest() {
		System.out.println(ic.get(STR_KEY));
		System.out.println(new String(ic.get(BYTE_KEY)));
	}
	
	@Test
	public void decrTest() {
		ic.decr(BYTE_KEY);
		ic.decr(STR_KEY);
	}
	
	@Test
	public void decrByTest() {
		ic.decrBy(BYTE_KEY, 3);
		ic.decrBy(STR_KEY, 3);
	}
	
	@Test
	public void delTest() {
		ic.del(BYTE_KEY);
		ic.del(STR_KEY);
	}
	
	@Test
	public void delSTest() {
		ic.del(STR_KEY,"ts");
		ic.del(BYTE_KEY,"tss".getBytes());
	}
	
	@Test
	public void existsTest() {
		System.out.println(ic.exists(BYTE_KEY));
		System.out.println(ic.exists(STR_KEY));
	}
	
	@Test
	public void expireTest() {
		System.out.println(ic.expire(BYTE_KEY, 5));
		System.out.println(ic.expire(STR_KEY, 5));
	}
	
	@Test
	public void mapTest() {
		Map<String,String> m1 = new HashMap<>();
		m1.put("m1", "1");
		m1.put("m3", "3");
		Map<byte[],byte[]> m2 = new HashMap<>();
		m2.put("m2".getBytes(), "2".getBytes());
		m2.put("m4".getBytes(), "4".getBytes());
		
		ic.hmset("map", m1);
		ic.hmset("mapT".getBytes(), m2);
		
		ic.hset("map", "m1", "2");
		ic.hset("mapT".getBytes(), "m2".getBytes(), "2".getBytes());
		
		ic.hsetnx("map", "m1", "3");
		ic.hsetnx("mapT".getBytes(), "m2".getBytes(), "9".getBytes());
		
		ic.hget("map", "m1");
		ic.hget("mapT".getBytes(), "m2".getBytes());
		
		ic.hmget("map", "m","m3");
		ic.hmget("mapT".getBytes(), "m2".getBytes(),"m4".getBytes());
		
		ic.hlen("map");
		ic.hlen("mapT".getBytes());
		
		ic.hgetAll("map");
		ic.hgetAll("mapT".getBytes());
		
		ic.hexists("map", "m1");
		ic.hexists("mapT".getBytes(), "m2".getBytes());
		
		ic.hdel("map", "m1");
		ic.hdel("mapT".getBytes(), "m2".getBytes());
		
		
	}
	
	@Test
	public void incrTest() {
		ic.incr(BYTE_KEY);
		ic.incrBy(BYTE_KEY, 7l);
		ic.incr(STR_KEY);
		ic.incrBy(STR_KEY, 9l);
	}
	
	@Test
	public void listTest() {
		ic.lpush("list", "1","2","3");
		ic.lpush("listT".getBytes(), "1".getBytes(),"2".getBytes(),"3".getBytes());
		
		ic.rpush("list", "2","1");
		ic.rpush("listT".getBytes(), "2".getBytes(),"1".getBytes());
		
		ic.llen("list");
		ic.llen("listT".getBytes());
		
		ic.lpop("list");
		ic.lpop("listT".getBytes());
		
		ic.lrangeAll("list");
		ic.lrangeAll("listT".getBytes());
		
		ic.rpop("list");
		ic.rpop("listT".getBytes());
	}
	
	@Test
	public void setTest() {
		ic.sadd("set1", "1","2","3","4");
		ic.sadd("setT1".getBytes(), "1".getBytes(),"2".getBytes(),"3".getBytes(),"4".getBytes());
		ic.sadd("set2", "1","2","3");
		ic.sadd("setT2".getBytes(), "1".getBytes(),"2".getBytes(),"3".getBytes());
		ic.scard("set1");
		ic.scard("setT1".getBytes());
		ic.sdiff("set1","set2");
		ic.sdiff("setT1".getBytes(),"setT2".getBytes());
		ic.sdiffstore("set3", "set1","set2");
		ic.sdiffstore("set3".getBytes(), "setT1".getBytes(),"setT2".getBytes());
		ic.smembers("set1");
		ic.smembers("setT1".getBytes());
		ic.srem("set1", "1","2");
		ic.srem("setT1".getBytes(), "1".getBytes(),"2".getBytes());
		ic.sunion("set1","set2");
		ic.sunion("setT1".getBytes(),"setT2".getBytes());
	}
	
	@Test
	public void ttl() {
		ic.ttl("setex");
		ic.ttl("seteX".getBytes());
	}
	
}
