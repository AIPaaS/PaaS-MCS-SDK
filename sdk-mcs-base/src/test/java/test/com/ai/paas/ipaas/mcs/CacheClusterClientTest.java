package test.com.ai.paas.ipaas.mcs;

import static org.junit.Assert.*;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ai.paas.ipaas.mcs.impl.CacheClusterClient;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

public class CacheClusterClientTest {
	private static ICacheClient client = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		String host = "10.1.235.24:6811,10.1.235.24:6812,10.1.235.24:6813,10.1.235.24:6814,10.1.235.24:6815,10.1.235.24:6816";
		String[] hosts = host.split(",");
		client = new CacheClusterClient(config, hosts,"asc123");
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
	public void testCacheClusterClientGenericObjectPoolConfigStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testCacheClusterClientGenericObjectPoolConfigStringArrayString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetStringString() {
		client.set("dxf", "123456");
		client.set("123456", "dxf");
		assertTrue("123456".equals(client.get("dxf")));
		assertTrue("dxf".equals(client.get("123456")));
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

	@Test
	public void testLremStringLongString() {
		fail("Not yet implemented");
	}

	@Test
	public void testLremByteArrayLongByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testZaddStringDoubleString() {
		fail("Not yet implemented");
	}

	@Test
	public void testZaddStringDoubleStringZAddParams() {
		fail("Not yet implemented");
	}

	@Test
	public void testZaddStringMapOfStringDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testZaddStringMapOfStringDoubleZAddParams() {
		fail("Not yet implemented");
	}

	@Test
	public void testZcountStringDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testZcountStringStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testZincrbyStringDoubleString() {
		fail("Not yet implemented");
	}

	@Test
	public void testZincrbyStringDoubleStringZIncrByParams() {
		fail("Not yet implemented");
	}

	@Test
	public void testZrange() {
		fail("Not yet implemented");
	}

	@Test
	public void testZrangeByScoreStringDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testZrangeByScoreStringStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testZrangeByScoreStringDoubleDoubleIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testZrevrange() {
		fail("Not yet implemented");
	}

	@Test
	public void testZrevrangeByScoreStringDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testZrevrangeByScoreStringStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testZrevrangeByScoreStringDoubleDoubleIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testZrevrank() {
		fail("Not yet implemented");
	}

	@Test
	public void testZrem() {
		fail("Not yet implemented");
	}

	@Test
	public void testZremrangeByRank() {
		fail("Not yet implemented");
	}

	@Test
	public void testZremrangeByScoreStringDoubleDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testZremrangeByScoreStringStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testAcquireLock() {
		fail("Not yet implemented");
	}

	@Test
	public void testReleaseLock() {
		fail("Not yet implemented");
	}

	@Test
	public void testPublish() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubscribe() {
		fail("Not yet implemented");
	}

	@Test
	public void testPsubscribe() {
		fail("Not yet implemented");
	}

	@Test
	public void testHkeysString() {
		fail("Not yet implemented");
	}

	@Test
	public void testHvalsString() {
		fail("Not yet implemented");
	}

	@Test
	public void testHkeysByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testHvalsByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testKeys() {
		fail("Not yet implemented");
	}

	@Test
	public void testMain() {
		fail("Not yet implemented");
	}

	@Test
	public void testClose() {
		fail("Not yet implemented");
	}

	@Test
	public void testStartTransaction() {
		fail("Not yet implemented");
	}

	@Test
	public void testCommitTransaction() {
		fail("Not yet implemented");
	}

	@Test
	public void testRollbackTransaction() {
		fail("Not yet implemented");
	}

	@Test
	public void testWatch() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnwatch() {
		fail("Not yet implemented");
	}

	@Test
	public void testObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClass() {
		fail("Not yet implemented");
	}

	@Test
	public void testHashCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testEquals() {
		fail("Not yet implemented");
	}

	@Test
	public void testClone() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotify() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotifyAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitLongInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testWait() {
		fail("Not yet implemented");
	}

	@Test
	public void testFinalize() {
		fail("Not yet implemented");
	}

}
