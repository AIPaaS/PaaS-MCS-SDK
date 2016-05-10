package test.com.ai.paas.ipaas.mcs;

import com.ai.paas.ipaas.mcs.CacheFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;

public class CacheClusterTest {
	private static final String AUTH_ADDR = "http://10.1.228.198:14815/iPaas-Web/audit/ckUser";
	private static AuthDescriptor ad = null;
	private static ICacheClient ic = null;
	
	static{
		ad =  new AuthDescriptor(AUTH_ADDR, "yinxiao", "F2B940BD8F7C45209D554E345E5D894A","cache1152");
		try {
			ic = CacheFactory.getClient(ad);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void addItemTest() {
//		ic.addItem("key", "hello");
//	}
//	
//	@Test
//	public void getItemTest() {
//		System.out.println(ic.getItem("key"));
//	}

}
