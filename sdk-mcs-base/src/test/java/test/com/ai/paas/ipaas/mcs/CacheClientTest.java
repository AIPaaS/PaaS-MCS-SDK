package test.com.ai.paas.ipaas.mcs;

import static org.junit.Assert.*;

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
		String host = "";
		client = new CacheClient(config, host);
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
	public void testSetStringString() {
		client.set("123", "123456");
		assertEquals("123456", client.get("123"));
		client.del("123");
	}

	@Test
	public void testSetexStringIntString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetString() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelString() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testExpireStringInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testExpireAtStringLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testTtlString() {
		fail("Not yet implemented");
	}

	@Test
	public void testExistsString() {
		fail("Not yet implemented");
	}

	@Test
	public void testIncrString() {
		fail("Not yet implemented");
	}

	@Test
	public void testIncrByStringLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecrString() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecrByStringLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testLpushStringStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testRpushStringStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testLremStringLongString() {
		fail("Not yet implemented");
	}

	@Test
	public void testLlenString() {
		fail("Not yet implemented");
	}

	@Test
	public void testLpopString() {
		fail("Not yet implemented");
	}

	@Test
	public void testRpopString() {
		fail("Not yet implemented");
	}

	@Test
	public void testLrangeStringLongLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testLrangeAllString() {
		fail("Not yet implemented");
	}

	@Test
	public void testHsetStringStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testHsetnxStringStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testHmsetStringMapOfStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testHgetStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testHmgetStringStringArray() {
		fail("Not yet implemented");
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
