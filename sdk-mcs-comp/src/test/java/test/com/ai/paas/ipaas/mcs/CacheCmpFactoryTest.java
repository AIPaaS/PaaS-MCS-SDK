package test.com.ai.paas.ipaas.mcs;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ai.paas.ipaas.mcs.CacheCmpFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

public class CacheCmpFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetClientProperties() {
		Properties p=new Properties();
		p.setProperty("mcs.mode", "sentinel");
		p.setProperty("mcs.maxtotal", "500");
		p.setProperty("mcs.maxIdle", "10");
		p.setProperty("mcs.testOnBorrow", "true");
		p.setProperty("mcs.host", "10.1.235.23:26379,10.1.235.22:26379,10.1.235.24:26379");
		
		ICacheClient client=CacheCmpFactory.getClient(p);
		client.set("test123456", "123456");
		assertEquals("123456", client.get("test123456"));
	}

	@Test
	public void testGetClient() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClientInputStream() {
		fail("Not yet implemented");
	}

}
