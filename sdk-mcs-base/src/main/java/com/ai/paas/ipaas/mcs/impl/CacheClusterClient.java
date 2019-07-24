package com.ai.paas.ipaas.mcs.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;

import com.ai.paas.GeneralRuntimeException;
import com.ai.paas.ipaas.mcs.ICacheClient;
import com.ai.paas.ipaas.mcs.exception.CacheException;
import com.ai.paas.util.Assert;
import com.ai.paas.util.StringUtil;
import com.google.common.collect.Lists;

import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions.RefreshTrigger;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisClusterException;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;

public class CacheClusterClient implements ICacheClient {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CacheClusterClient.class);
    @SuppressWarnings("rawtypes")
    private GenericObjectPoolConfig config;
    private String[] hosts;
    private String pwd;
    private JedisCluster jc;
    private boolean isRedisNeedAuth = false;
    GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool = null;

    StatefulRedisClusterConnection<String, String> connection = null;

    RedisClusterClient clusterClient = null;

    private static final String UNSUPPORTED = "Unsupported Feature!";

    @SuppressWarnings("rawtypes")
    public CacheClusterClient(GenericObjectPoolConfig config, String[] hosts) {
        this.config = config;
        this.hosts = hosts;
        getCluster();
    }

    @SuppressWarnings("rawtypes")
    public CacheClusterClient(GenericObjectPoolConfig config, String[] hosts, String pwd) {
        this.config = config;
        this.hosts = hosts;
        if (!StringUtil.isBlank(pwd)) {
            this.pwd = pwd;
            isRedisNeedAuth = true;
        }

        getCluster();

    }

    private void getCluster() {
        log.info("-----------------------创建JedisPool------------------------begin---");
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        try {
            for (String address : hosts) {
                String[] ipAndPort = address.split(":");
                jedisClusterNodes.add(new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
                log.debug(address);
            }
            if (config.getMaxWaitMillis() < 20000)
                config.setMaxWaitMillis(20000);
            if (isRedisNeedAuth)
                jc = new JedisCluster(jedisClusterNodes, 20000, 20000, 20000, pwd, config);
            else
                jc = new JedisCluster(jedisClusterNodes, 20000, 20000, config);
        } catch (Exception e) {
            throw new CacheException(e);
        }
        log.info("-----------------------创建JedisPool------------------------end---");
        log.info("-----------------------创建LettucePool------------------------begin---");
        createLettuceClientPool();
        log.info("-----------------------创建LettucePool------------------------end---");
    }

    private void createLettuceClientPool() {
        List<RedisURI> redisURIS = new ArrayList<>();
        for (String address : hosts) {
            String[] ipAndPort = address.split(":");
            if (isRedisNeedAuth)
                redisURIS.add(
                        RedisURI.Builder.redis(ipAndPort[0], Integer.parseInt(ipAndPort[1])).withPassword(pwd).build());
            else
                redisURIS.add(RedisURI.Builder.redis(ipAndPort[0], Integer.parseInt(ipAndPort[1])).build());
        }
        if (config.getMaxWaitMillis() < 20000)
            config.setMaxWaitMillis(20000);
        clusterClient = RedisClusterClient.create(redisURIS);
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enableAdaptiveRefreshTrigger(RefreshTrigger.MOVED_REDIRECT, RefreshTrigger.PERSISTENT_RECONNECTS)
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(30)).build();

        clusterClient.setOptions(ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build());

        pool = ConnectionPoolSupport.createGenericObjectPool(() -> clusterClient.connect(), config);
        connection = getLettuceCnn();
    }

    private StatefulRedisClusterConnection<String, String> getLettuceCnn() {
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    /**
     * redis是否可用
     *
     * @return
     */
    private boolean canConnection() {
        try {
            jc.get("ok");
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    public String set(String key, String value) {
        try {
            return jc.set(key, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return set(key, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public String setex(String key, int seconds, String value) {
        try {
            return jc.setex(key, seconds, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return setex(key, seconds, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public String get(String key) {
        try {
            return jc.get(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return get(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long del(String key) {
        try {
            return jc.del(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return del(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long del(String... keys) {
        try {
            return jc.del(keys);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return del(keys);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long expire(String key, int seconds) {
        try {
            return jc.expire(key, seconds);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return expire(key, seconds);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    @Override
    public Long expireAt(String key, long seconds) {
        return expireAt(key.getBytes(), seconds);
    }

    public Long ttl(String key) {
        try {
            return jc.ttl(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return ttl(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public boolean exists(String key) {
        try {
            return jc.exists(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return exists(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long incr(String key) {
        try {
            return jc.incr(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return incr(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long incrBy(String key, long increment) {
        try {
            return jc.incrBy(key, increment);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return incrBy(key, increment);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long decr(String key) {
        try {
            return jc.decr(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return decr(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long decrBy(String key, long decrement) {
        try {
            return jc.decrBy(key, decrement);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return decrBy(key, decrement);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long lpush(String key, String... strings) {
        try {
            return jc.lpush(key, strings);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return lpush(key, strings);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long rpush(String key, String... strings) {
        try {
            return jc.rpush(key, strings);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return rpush(key, strings);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long llen(String key) {
        try {
            return jc.llen(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return llen(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public String lpop(String key) {
        try {
            return jc.lpop(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return lpop(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public String rpop(String key) {
        try {
            return jc.rpop(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return rpop(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public List<String> lrange(String key, long start, long end) {
        try {
            return jc.lrange(key, start, end);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return lrange(key, start, end);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public List<String> lrangeAll(String key) {
        try {
            return jc.lrange(key, 0, -1);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return lrange(key, 0, -1);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long hset(String key, String field, String value) {
        try {
            return jc.hset(key, field, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hset(key, field, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long hsetnx(String key, String field, String value) {
        try {
            return jc.hsetnx(key, field, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hsetnx(key, field, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public String hmset(String key, Map<String, String> hash) {
        try {
            return jc.hmset(key, hash);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hmset(key, hash);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public String hget(String key, String field) {
        try {
            return jc.hget(key, field);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hget(key, field);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public List<String> hmget(final String key, final String... fields) {
        try {
            return jc.hmget(key, fields);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hmget(key, fields);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Boolean hexists(String key, String field) {
        try {
            return jc.hexists(key, field);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hexists(key, field);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long hdel(String key, String... fields) {
        try {
            return jc.hdel(key, fields);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hdel(key, fields);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long hlen(String key) {
        try {
            return jc.hlen(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hlen(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Map<String, String> hgetAll(String key) {
        try {
            return jc.hgetAll(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hgetAll(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long sadd(String key, String... members) {
        try {
            return jc.sadd(key, members);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return sadd(key, members);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Set<String> smembers(String key) {
        try {
            return jc.smembers(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return smembers(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long srem(String key, String... members) {
        try {
            return jc.srem(key, members);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return srem(key, members);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long scard(String key) {
        try {
            return jc.scard(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return scard(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Set<String> sunion(String... keys) {
        try {
            return jc.sunion(keys);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return sunion(keys);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Set<String> sdiff(String... keys) {
        try {
            return jc.sdiff(keys);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return sdiff(keys);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long sdiffstore(String dstkey, String... keys) {
        try {
            return jc.sdiffstore(dstkey, keys);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return sdiffstore(dstkey, keys);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public String set(byte[] key, byte[] value) {
        try {
            return jc.set(key, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return set(key, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public String setex(byte[] key, int seconds, byte[] value) {
        try {
            return jc.setex(key, seconds, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return setex(key, seconds, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public byte[] get(byte[] key) {
        try {
            return jc.get(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return get(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long del(byte[] key) {
        try {
            return jc.del(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return del(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long del(byte[]... keys) {
        try {
            return jc.del(keys);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return del(keys);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long expire(byte[] key, int seconds) {
        try {
            return jc.expire(key, seconds);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return expire(key, seconds);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long expireAt(byte[] key, long seconds) {
        try {
            return jc.expireAt(key, seconds);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return expireAt(key, seconds);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long ttl(byte[] key) {
        try {
            return jc.ttl(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return ttl(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public boolean exists(byte[] key) {
        try {
            return jc.exists(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return exists(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long incr(byte[] key) {
        try {
            return jc.incr(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return incr(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {

            throw new CacheException(e);
        }
    }

    public Long incrBy(byte[] key, long increment) {
        try {
            return jc.incrBy(key, increment);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return incrBy(key, increment);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long decr(byte[] key) {
        try {
            return jc.decr(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return decr(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long decrBy(byte[] key, long decrement) {
        try {
            return jc.decrBy(key, decrement);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return decrBy(key, decrement);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long lpush(byte[] key, byte[]... strings) {
        try {
            return jc.lpush(key, strings);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return lpush(key, strings);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long rpush(byte[] key, byte[]... strings) {
        try {
            return jc.rpush(key, strings);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return rpush(key, strings);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long llen(byte[] key) {
        try {
            return jc.llen(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return llen(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public byte[] lpop(byte[] key) {
        try {
            return jc.lpop(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return lpop(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public byte[] rpop(byte[] key) {
        try {
            return jc.rpop(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return rpop(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public List<byte[]> lrange(byte[] key, long start, long end) {
        try {
            return jc.lrange(key, start, end);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return lrange(key, start, end);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public List<byte[]> lrangeAll(byte[] key) {
        try {
            return jc.lrange(key, 0, -1);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return lrange(key, 0, -1);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long hset(byte[] key, byte[] field, byte[] value) {
        try {
            return jc.hset(key, field, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hset(key, field, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long hsetnx(byte[] key, byte[] field, byte[] value) {
        try {
            return jc.hsetnx(key, field, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hsetnx(key, field, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long setnx(byte[] key, byte[] value) {
        try {
            return jc.setnx(key, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return jc.setnx(key, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long setnx(String key, String value) {
        return setnx(key.getBytes(), value.getBytes());
    }

    public String hmset(byte[] key, Map<byte[], byte[]> hash) {
        try {
            return jc.hmset(key, hash);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hmset(key, hash);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public byte[] hget(byte[] key, byte[] field) {
        try {
            return jc.hget(key, field);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hget(key, field);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public List<byte[]> hmget(final byte[] key, final byte[]... fields) {
        try {
            return jc.hmget(key, fields);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hmget(key, fields);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Boolean hexists(byte[] key, byte[] field) {
        try {
            return jc.hexists(key, field);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hexists(key, field);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long hdel(byte[] key, byte[]... fields) {
        try {
            return jc.hdel(key, fields);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hdel(key, fields);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long hlen(byte[] key) {
        try {
            return jc.hlen(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hlen(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Map<byte[], byte[]> hgetAll(byte[] key) {
        try {
            return jc.hgetAll(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hgetAll(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long sadd(byte[] key, byte[]... members) {
        try {
            return jc.sadd(key, members);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return sadd(key, members);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Set<byte[]> smembers(byte[] key) {
        try {
            return jc.smembers(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return smembers(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long srem(byte[] key, byte[]... members) {
        try {
            return jc.srem(key, members);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return srem(key, members);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long scard(byte[] key) {
        try {
            return jc.scard(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return scard(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Set<byte[]> sunion(byte[]... keys) {
        try {
            return jc.sunion(keys);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return sunion(keys);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Set<byte[]> sdiff(byte[]... keys) {
        try {
            return jc.sdiff(keys);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return sdiff(keys);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public Long sdiffstore(byte[] dstkey, byte[]... keys) {
        try {
            return jc.sdiffstore(dstkey, keys);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return sdiffstore(dstkey, keys);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
        try {
            return jc.hincrBy(key, field, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hincrBy(key, field, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Double incrByFloat(String key, double value) {
        try {
            return jc.incrByFloat(key, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return incrByFloat(key, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Double hincrByFloat(String key, String field, double value) {
        try {
            return jc.hincrByFloat(key.getBytes(), field.getBytes(), value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hincrByFloat(key, field, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long lrem(String key, long count, String value) {
        try {
            return jc.lrem(key, count, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return lrem(key, count, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long lrem(byte[] key, long count, byte[] value) {
        try {
            return jc.lrem(key, count, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return lrem(key, count, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zadd(String key, double score, String member) {
        try {
            return jc.zadd(key, score, member);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zadd(key, score, member);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zadd(final String key, final double score, final String member, final ZAddParams params) {
        try {
            return jc.zadd(key, score, member, params);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zadd(key, score, member, params);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        try {
            return jc.zadd(key, scoreMembers);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zadd(key, scoreMembers);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zadd(final String key, final Map<String, Double> scoreMembers, final ZAddParams params) {
        try {
            return jc.zadd(key, scoreMembers, params);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zadd(key, scoreMembers, params);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zcount(final String key, final double min, final double max) {
        try {
            return jc.zcount(key, min, max);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zcount(key, min, max);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zcount(final String key, final String min, final String max) {
        try {
            return jc.zcount(key, min, max);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zcount(key, min, max);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Double zincrby(final String key, final double score, final String member) {
        try {
            return jc.zincrby(key, score, member);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zincrby(key, score, member);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Double zincrby(String key, double score, String member, ZIncrByParams params) {
        try {
            return jc.zincrby(key, score, member, params);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zincrby(key, score, member, params);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Set<String> zrange(final String key, final long start, final long end) {
        try {
            return jc.zrange(key, start, end);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zrange(key, start, end);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        try {
            return jc.zrangeByScore(key, min, max);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zrangeByScore(key, min, max);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Set<String> zrangeByScore(final String key, final String min, final String max) {
        try {
            return jc.zrangeByScore(key, min, max);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zrangeByScore(key, min, max);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset,
            int count) {
        try {
            return jc.zrangeByScore(key, min, max, offset, count);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zrangeByScore(key, min, max, offset, count);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Set<String> zrevrange(final String key, final long start, final long end) {
        try {
            return jc.zrevrange(key, start, end);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zrevrange(key, start, end);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
        try {
            return jc.zrevrangeByScore(key, max, min);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zrevrangeByScore(key, max, min);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Set<String> zrevrangeByScore(final String key, final String max, final String min) {
        try {
            return jc.zrevrangeByScore(key, max, min);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zrevrangeByScore(key, max, min);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset,
            int count) {
        try {
            return jc.zrevrangeByScore(key, max, min, offset, count);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zrevrangeByScore(key, max, min, offset, count);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zrevrank(final String key, final String member) {
        try {
            return jc.zrevrank(key, member);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zrevrank(key, member);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zrem(final String key, final String... member) {
        try {
            return jc.zrem(key, member);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zrem(key, member);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zremrangeByRank(final String key, final long start, final long end) {
        try {
            return jc.zremrangeByRank(key, start, end);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zremrangeByRank(key, start, end);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zremrangeByScore(final String key, final double start, final double end) {
        try {
            return jc.zremrangeByScore(key, start, end);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zremrangeByScore(key, start, end);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Long zremrangeByScore(final String key, final String start, final String end) {
        try {
            return jc.zremrangeByScore(key, start, end);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return zremrangeByScore(key, start, end);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public String acquireLock(String lockName, long acquireTimeoutInMS, long lockTimeoutInMS) {
        getCluster();
        String retIdentifier = null;
        try {
            String identifier = UUID.randomUUID().toString();
            String lockKey = "lock:" + lockName;
            int lockExpire = (int) (lockTimeoutInMS / 1000);

            long end = System.currentTimeMillis() + acquireTimeoutInMS;
            while (System.currentTimeMillis() < end) {
                if (jc.setnx(lockKey, identifier) == 1) {
                    jc.expire(lockKey, lockExpire);
                    retIdentifier = identifier;
                }
                if (jc.pttl(lockKey) == -1) {
                    jc.expire(lockKey, lockExpire);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (JedisClusterException jcException) {
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
        return retIdentifier;
    }

    @Override
    public boolean releaseLock(String lockName, String identifier) {
        getCluster();
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            /** if key had bean timeout, return true. **/
            if (jc.pttl(lockKey) == -2) {
                retFlag = true;
                return retFlag;
            }
            if (identifier.equals(jc.get(lockKey))) {
                jc.del(lockKey);
                retFlag = true;
            }
        } catch (JedisClusterException jcException) {
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }

        return retFlag;
    }

    @Override
    public Long publish(final String channel, final String message) {
        try {
            return jc.publish(channel, message);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return publish(channel, message);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
        try {
            jc.subscribe(jedisPubSub, channels);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                subscribe(jedisPubSub, channels);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
        try {
            jc.psubscribe(jedisPubSub, patterns);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                psubscribe(jedisPubSub, patterns);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Set<String> hkeys(String key) {
        try {
            return jc.hkeys(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hkeys(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }

    }

    @Override
    public List<String> hvals(String key) {
        try {
            return jc.hvals(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hvals(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }

    }

    @Override
    public Set<byte[]> hkeys(byte[] key) {
        try {
            return jc.hkeys(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hkeys(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }

    }

    @Override
    public Collection<byte[]> hvals(byte[] key) {
        try {
            return jc.hvals(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hvals(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Set<String> keys(String pattern) {
        // 集群模式不支持
        return new TreeSet<>();
    }

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {
        String[] hosts = { "10.1.245.224:36379", "10.1.245.224:36380", "10.1.245.224:36381", "10.1.245.224:36382",
                "10.1.245.224:36383", "10.1.245.8:36384" };
        ICacheClient client = new CacheClusterClient(new GenericObjectPoolConfig(), hosts, "123456");
        log.info(client.get("dxf"));
    }

    @Override
    public void close() {
        if (null != jc) {
            jc.close();
        }
        if (null != pool)
            pool.close();
        if (null != clusterClient)
            clusterClient.shutdown();
    }

    @Override
    public Transaction startTransaction() {
        throw new CacheException(UNSUPPORTED);
    }

    @Override
    public void commitTransaction(Transaction tx) {
        throw new CacheException(UNSUPPORTED);
    }

    @Override
    public void rollbackTransaction(Transaction tx) {
        throw new CacheException(UNSUPPORTED);
    }

    @Override
    public void watch(String... keys) {
        throw new CacheException(UNSUPPORTED);
    }

    @Override
    public void unwatch() {
        throw new CacheException(UNSUPPORTED);
    }

    @Override
    public Boolean setBit(String key, long offset, String value) {
        try {
            return jc.setbit(key, offset, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return setBit(key, offset, value);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Boolean getBit(String key, long offset) {
        try {
            return jc.getbit(key, offset);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return getBit(key, offset);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public long countBit(String key, long start, long end) {
        try {
            return jc.bitcount(key, start, end);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return countBit(key, start, end);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public long countBit(String key) {
        try {
            return jc.bitcount(key);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return countBit(key);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public long addGeo(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        try {
            return jc.geoadd(key, memberCoordinateMap);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return addGeo(key, memberCoordinateMap);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public long addGeo(String key, String name, long longitude, long latitude) {
        try {
            return jc.geoadd(key, longitude, latitude, name);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return addGeo(key, name, longitude, latitude);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public List<GeoCoordinate> getGeo(String key, String... members) {
        try {
            return jc.geopos(key, members);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return getGeo(key, members);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Double getGeoDist(String key, String start, String end) {
        try {
            return jc.geodist(key, start, end);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return getGeoDist(key, start, end);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Double getGeoDist(String key, String start, String end, GeoUnit unit) {
        try {
            return jc.geodist(key, start, end, unit);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return getGeoDist(key, start, end, unit);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public List<GeoRadiusResponse> getGeoDist(String key, long longitude, long latitude, long radius, GeoUnit unit) {
        try {
            return jc.georadius(key, longitude, latitude, radius, unit);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return getGeoDist(key, longitude, latitude, radius, unit);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public List<GeoRadiusResponse> getGeoDist(String key, double longitude, double latitude, double radius,
            GeoUnit unit, GeoRadiusParam param) {
        try {
            return jc.georadius(key, longitude, latitude, radius, unit, param);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return getGeoDist(key, longitude, latitude, radius, unit, param);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public List<GeoRadiusResponse> getGeoDist(String key, String member, long radius, GeoUnit unit) {
        try {
            return jc.georadiusByMember(key, member, radius, unit);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return getGeoDist(key, member, radius, unit);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public List<GeoRadiusResponse> getGeoDist(String key, String member, double radius, GeoUnit unit,
            GeoRadiusParam param) {
        try {
            return jc.georadiusByMember(key, member, radius, unit, param);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return getGeoDist(key, member, radius, unit, param);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public List<String> getGeoHash(String key, String... members) {
        try {
            return jc.geohash(key, members);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return getGeoHash(key, members);
            }
            throw new CacheException(jcException);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public List<String> mget(String... keys) {
        List<Object> result = pipelineGet(keys);
        List<String> list = new ArrayList<>();
        result.forEach(e -> list.add(e.toString()));
        return list;
    }

    @Override
    public void mset(Map<String, String> values) {
        Assert.notNull(values);
        pipelineSet(values);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Object> pipelineGet(String... keys) {
        long start = System.currentTimeMillis();
        if (null == connection)
            connection = getLettuceCnn();
        log.info("get lettuce redis client connection used:{}", System.currentTimeMillis() - start);
        RedisAdvancedClusterAsyncCommands<String, String> asyncCommands = connection.async();
        start = System.currentTimeMillis();
        asyncCommands.setAutoFlushCommands(false);
        List<RedisFuture<?>> futures = Lists.newArrayList();
        for (String key : keys) {
            futures.add(asyncCommands.get(key));
        }
        asyncCommands.flushCommands();
        RedisFuture[] results = new RedisFuture[futures.size()];
        boolean result = LettuceFutures.awaitAll(5, TimeUnit.SECONDS, futures.toArray(results));
        log.info("get pipe  used:{}", System.currentTimeMillis() - start);
        if (result) {
            try {
                return assemble(results);
            } catch (Exception e) {
                throw new CacheException(e);
            }
        } else {
            LettuceFutures.awaitAll(5, TimeUnit.SECONDS, futures.toArray(results));
            try {
                return assemble(results);
            } catch (Exception e) {
                throw new CacheException(e);
            }
        }

    }

    @SuppressWarnings("rawtypes")
    private List<Object> assemble(RedisFuture[] results) throws Exception {
        long start = System.currentTimeMillis();
        List<Object> list = new ArrayList<>();
        for (RedisFuture result : results) {
            list.add(result.get());
        }
        log.info("assemble result used:{}", System.currentTimeMillis() - start);
        return list;
    }

    @Override
    public void pipelineSet(Map<String, String> values) {
        long start = System.currentTimeMillis();
        if (null == connection)
            connection = getLettuceCnn();
        log.info("get lettuce redis client connection used:{}", System.currentTimeMillis() - start);
        RedisAdvancedClusterAsyncCommands<String, String> asyncCommands = connection.async();
        start = System.currentTimeMillis();
        asyncCommands.setAutoFlushCommands(false);
        List<RedisFuture<?>> futures = Lists.newArrayList();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            futures.add(asyncCommands.set(entry.getKey(), entry.getValue()));
        }
        asyncCommands.flushCommands();
        LettuceFutures.awaitAll(5, TimeUnit.SECONDS, futures.toArray(new RedisFuture[futures.size()]));
        log.info(" lettuce redis set  used:{}", System.currentTimeMillis() - start);
    }

    @Override
    public Pipeline startPipeline() {
        throw new GeneralRuntimeException("unimplemented yet!");
    }

    @Override
    public void endPipeline(Pipeline p) {
        throw new GeneralRuntimeException("unimplemented yet!");
    }
}
