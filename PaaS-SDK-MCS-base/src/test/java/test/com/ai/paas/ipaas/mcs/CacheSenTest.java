package test.com.ai.paas.ipaas.mcs;

import org.junit.Test;

import com.ai.paas.ipaas.mcs.CacheFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;

public class CacheSenTest {
	private static final String AUTH_ADDR = "http://10.1.228.198:14821/iPaas-Auth/service/auth";
//	private static final String AUTH_ADDR = "http://10.1.31.20:19821/iPaas-Auth/service/check";
		
	private static AuthDescriptor ad = null;
	private static ICacheClient ic = null;
	
	private static final String STR_KEY = "test";
	private static final byte[] BYTE_KEY = "testSet".getBytes();
	
	
	static{
		ad =  new AuthDescriptor(AUTH_ADDR, "FFF49D0D518948D0AB28D7A8EEE25D03", "679328","602");
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
	
}
