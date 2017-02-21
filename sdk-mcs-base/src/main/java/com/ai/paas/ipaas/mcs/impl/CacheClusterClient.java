package com.ai.paas.ipaas.mcs.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;

import com.ai.paas.ipaas.mcs.exception.CacheClientException;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisClusterException;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

public class CacheClusterClient implements ICacheClient {

    private static transient final org.slf4j.Logger log = LoggerFactory.getLogger(CacheClusterClient.class);
    private GenericObjectPoolConfig config;
    private String[] hosts;
    @SuppressWarnings("unused")
	private final String preKey = CacheHelper.preKey();
    @SuppressWarnings("unused")
	private String pwd;
    private JedisCluster jc;
    @SuppressWarnings("unused")
	private boolean isRedisNeedAuth = false;

    public CacheClusterClient(GenericObjectPoolConfig config, String[] hosts) {
        this.config = config;
        this.hosts = hosts;
    }

    public CacheClusterClient(GenericObjectPoolConfig config, String[] hosts, String pwd) {
        this(config, hosts);
        this.pwd = pwd;
        getCluster();
        isRedisNeedAuth = true;
    }

    private void getCluster() {
        log.info("-----------------------创建JedisPool------------------------begin---");
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        try {
            for (String address : hosts) {
                String[] ipAndPort = address.split(":");
                jedisClusterNodes.add(new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
                log.debug(address);
            }
            if (config.getMaxWaitMillis() < 20000)
                config.setMaxWaitMillis(20000);
            jc = new JedisCluster(jedisClusterNodes, 20000, config);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("-----------------------创建JedisPool------------------------end---");
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
            log.error(e.getMessage(), e);
            return false;
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
        }
    }

    @Override
    public Double hincrByFloat(String key, String field, double value) {
        try {
            return jc.hincrByFloat(key, field, value);
        } catch (JedisClusterException jcException) {
            getCluster();
            if (canConnection()) {
                return hincrByFloat(key, field, value);
            }
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
		}
	}

	@Override
	public Set<String> zrangeByScore(final String key, final String min, final String max){
		try {
			return jc.zrangeByScore(key, min, max);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return zrangeByScore(key, min, max);
			}
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
		}
    }

	@Override
	public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset, int count) {
		try {
			return jc.zrangeByScore(key, min, max, offset, count);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return zrangeByScore(key, min, max, offset, count);
			}
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
		}
    }

	@Override
	public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset, int count) {
		try {
			return jc.zrevrangeByScore(key, max, min, offset, count);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return zrevrangeByScore(key, max, min, offset, count);
			}
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        }
		
		return retIdentifier;
	}
	
	@Override
	public boolean releaseLock(String lockName, String identifier) {
		getCluster();
		String lockKey = "lock:" + lockName;
		boolean retFlag = false;
		try {
			/** if key had bean timeout, return true.**/
			if (jc.pttl(lockKey) == -2) {
				retFlag = true;
				return retFlag;
			}
			if (identifier.equals(jc.get(lockKey))) {
				jc.del(lockKey);
				retFlag = true;
			}
		} catch (JedisClusterException jcException) {
            log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
		}
	}
	
	@Override
	public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns)  {
		try {
			jc.psubscribe(jedisPubSub, patterns);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				psubscribe(jedisPubSub, patterns);
			}
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
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
			log.error(jcException.getMessage(), jcException);
            throw new CacheClientException(jcException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {
		}
	}
	
	public static void main(String[] args){
		String[] hosts={"10.1.245.224:36379","10.1.245.224:36380","10.1.245.224:36381","10.1.245.224:36382","10.1.245.224:36383","10.1.245.8:36384"};
		ICacheClient client=new CacheClusterClient(new GenericObjectPoolConfig(), hosts,"123456");
		//client.set("dxf", "123456");
		System.out.println(client.get("dxf"));
	}
	
}
