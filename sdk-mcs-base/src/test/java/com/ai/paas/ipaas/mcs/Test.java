package com.ai.paas.ipaas.mcs;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.google.common.collect.Lists;

import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.support.ConnectionPoolSupport;

public class Test {

    public static void main(String[] args) throws Exception {
        String passwd = "QAZ234WSx";
        final RedisURI redisURI1 = RedisURI.Builder.redis("10.12.2.144", 11000).withPassword(passwd).build();
        final RedisURI redisURI2 = RedisURI.Builder.redis("10.12.2.144", 11001).withPassword(passwd).build();
        final RedisURI redisURI3 = RedisURI.Builder.redis("10.12.2.145", 11000).withPassword(passwd).build();
        final RedisURI redisURI4 = RedisURI.Builder.redis("10.12.2.145", 11001).withPassword(passwd).build();
        final RedisURI redisURI5 = RedisURI.Builder.redis("10.12.2.146", 11000).withPassword(passwd).build();
        final RedisURI redisURI6 = RedisURI.Builder.redis("10.12.2.146", 11001).withPassword(passwd).build();
        final List<RedisURI> redisURIS = Arrays.asList(redisURI1, redisURI2, redisURI3, redisURI4, redisURI5,
                redisURI6);
        final RedisClusterClient clusterClient = RedisClusterClient.create(redisURIS);

        GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool = ConnectionPoolSupport
                .createGenericObjectPool(() -> clusterClient.connect(), new GenericObjectPoolConfig());

        // execute work
        try (StatefulRedisClusterConnection<String, String> connection = pool.borrowObject()) {
            connection.sync().set("key", "value");
            connection.sync().blpop(10, "list");
            System.out.println(connection.sync().get("key"));

            // 开始测试插入
            RedisAdvancedClusterAsyncCommands<String, String> asyncCommands = connection.async();
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                asyncCommands.set("hhhh" + i, "hhhh" + i);
            }
            asyncCommands.flushCommands();
            System.out.println("--------1-------" + (System.currentTimeMillis() - start));
            System.out.println("-------1--------" + connection.sync().get("hhhh2"));
            start = System.currentTimeMillis();
            asyncCommands.setAutoFlushCommands(false);
            List<RedisFuture<?>> futures = Lists.newArrayList();
            for (int i = 0; i < 1000; i++) {
                futures.add(asyncCommands.get("hhhh" + i));
            }
            asyncCommands.flushCommands();

            // synchronization example: Wait until all futures complete
            RedisFuture[] results = new RedisFuture[futures.size()];
            boolean result = LettuceFutures.awaitAll(5, TimeUnit.SECONDS, futures.toArray(results));

            System.out.println("---------22------" + results[0].get());

            System.out.println("---------------" + (System.currentTimeMillis() - start));
        }
        // terminating
        pool.close();
        clusterClient.shutdown();
    }

}
