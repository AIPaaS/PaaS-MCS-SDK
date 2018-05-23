package test.com.ai.paas.ipaas.mcs;

import java.util.Date;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.ai.paas.ipaas.mcs.impl.CacheClient;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

public class Test {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		Date d = new Date();
		d.setTime(1524331452664247L);
		System.out.println(d);
		System.out.println(d.getYear());
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		String host = "10.15.16.130:9801";
		ICacheClient client = new CacheClient(config, host, "asc123");
		Thread t = null;
		for (int i = 0; i < 100; i++) {
			t = new RedisThread(client);
			t.start();
		}
		t.join();
	}

}
