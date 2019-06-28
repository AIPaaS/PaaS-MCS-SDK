package test.com.ai.paas.ipaas.mcs;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import com.ai.paas.ipaas.mcs.CacheCmpFactory;
import com.ai.paas.ipaas.mcs.ICacheClient;

public class CacheCmpFactoryTest {

    @Test
    public void testGetClientProperties() {
        Properties p = new Properties();
        p.setProperty("mcs.mode", "sentinel");
        p.setProperty("mcs.maxtotal", "500");
        p.setProperty("mcs.maxIdle", "10");
        p.setProperty("mcs.password", "asc123");
        p.setProperty("mcs.testOnBorrow", "true");
        p.setProperty("mcs.host", "10.15.16.130:12002");

        ICacheClient client = CacheCmpFactory.getClient(p);
        client.set("test123456", "123456");
        assertEquals("123456", client.get("test123456"));
    }

}
