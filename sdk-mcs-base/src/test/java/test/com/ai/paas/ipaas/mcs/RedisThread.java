package test.com.ai.paas.ipaas.mcs;

import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

import redis.clients.jedis.Transaction;

class RedisThread extends Thread {
	ICacheClient client = null;

	RedisThread(ICacheClient client) {
		this.client = client;
	}

	@Override
	public void run() {
		for (int i = 1; i < 100; i++) {
			client.set("ddd" + i, "wwww" + i);
			System.out.println(client.get("ddd" + i));
		}

		Transaction tx = client.startTransaction();
		tx.set("qqqq", "123456");
		tx.incrBy("dxf", 123L);
		client.commitTransaction(tx);
		System.out.println(client.get("qqqq"));
		System.out.println(client.get("dxf"));

		client.watch("dxf");
		Transaction tx1 = client.startTransaction();
		tx1.set("dxf", "12345678");
		client.commitTransaction(tx1);
		client.unwatch();
		System.out.println(client.get("dxf"));
	}

}