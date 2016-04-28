package com.ai.paas.ipaas.mcs.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.ai.paas.ipaas.mcs.exception.CacheClientException;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.util.CiperUtil;

public class CacheSentinelClient implements ICacheClient {

    private static transient final org.slf4j.Logger log = LoggerFactory.getLogger(CacheClient.class);
    private JedisSentinelPool pool;
    private GenericObjectPoolConfig config;
    private static final int TIMEOUT_KEY = 15000;
    private String host;
    private final String preKey = CacheHelper.preKey();
    private String pwd;
    private boolean isRedisNeedAuth = false;

    public CacheSentinelClient(GenericObjectPoolConfig config, String host) {
        this.config = config;
        this.host = host;
        createPool();
    }

    public CacheSentinelClient(GenericObjectPoolConfig config, String host, String pwd) {
        this.config = config;
        this.host = host;
        this.pwd = pwd;
        createPool();
        isRedisNeedAuth = true;
    }

    private synchronized void createPool() {
        if (!canConnection()) {
            log.info("Create JedisPool Begin ...");
            try {
                if (config.getMaxWaitMillis() < 15000)
                    config.setMaxWaitMillis(15000);
                
                Set<String> sentinels = new HashSet<String>(Arrays.asList(host.split(";")));
              //TODO
                String password=CiperUtil.decrypt("BaryTukyTukyBary",pwd);
                //String password="593802";
                pool=new JedisSentinelPool("mymaster", sentinels,config,TIMEOUT_KEY,password);
                if (canConnection())
                    log.info("Redis Server Info:" + host);
                log.info("Create JedisPool Done ...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * redis是否可用
     *
     * @return
     */
    private boolean canConnection() {
        if (pool == null)
            return false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.connect();
            jedis.get("ok");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
        return true;
    }

    private Jedis getJedis() {
        return pool.getResource();
    }

    private void returnResource(Jedis jedis) {
        pool.returnResourceObject(jedis);
    }

    public void destroyPool() {
        if (null != pool) {
            pool.destroy();
        }
    }

    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.set(key, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return set(key, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }

    }

    public String setex(String key, int seconds, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.setex(key, seconds, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return setex(key, seconds, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        byte[] data = null;
        try {
            jedis = getJedis();
            return jedis.get(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return get(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.del(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return del(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long hincrBy(String key, String field, long value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hincrBy(key, field, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hincrBy(key,field, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    @Override
    public Double hincrByFloat(String key, String field, double value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hincrByFloat(key, field, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hincrByFloat(key,field, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long del(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.del(keys);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return del(keys);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long expire(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.expire((key), seconds);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return expire(key, seconds);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long ttl(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.ttl(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return ttl(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.exists(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return exists(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.incr(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return incr(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long incrBy(String key, long increment) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.incrBy(key, increment);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return incrBy(key, increment);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long decr(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.decr(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return decr(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long decrBy(String key, long decrement) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.decrBy(key, decrement);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return decrBy(key, decrement);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long lpush(String key, String... strings) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lpush(key, strings);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return lpush(key, strings);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long rpush(String key, String... strings) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.rpush(key, strings);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return rpush(key, strings);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long llen(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.llen(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return llen(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public String lpop(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lpop(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return lpop(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public String rpop(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.rpop(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return rpop(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lrange(key, start, end);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return lrange(key, start, end);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public List<String> lrangeAll(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lrange(key, 0, -1);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return lrangeAll(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hset(key, field, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hset(key, field, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long hsetnx(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hsetnx(key, field, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hsetnx(key, field, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hmset(key, hash);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hmset(key, hash);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public String hget(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hget(key, field);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hget(key, field);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public List<String> hmget(final String key, final String... fields) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hmget(key, fields);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hmget(key, fields);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Boolean hexists(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hexists(key, field);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hexists(key, field);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long hdel(String key, String... fields) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hdel(key, fields);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hdel(key, fields);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long hlen(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hlen(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hlen(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hgetAll(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hgetAll(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long sadd(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sadd(key, members);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return sadd(key, members);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Set<String> smembers(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.smembers(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return smembers(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long srem(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.srem(key, members);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return srem(key, members);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.scard(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return scard(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Set<String> sunion(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sunion(keys);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return sunion(keys);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Set<String> sdiff(String... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sdiff(keys);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return sdiff(keys);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long sdiffstore(String dstkey, String... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sdiffstore(dstkey, keys);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return sdiffstore(dstkey, keys);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public String set(byte[] key, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.set(key, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return set(key, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public String setex(byte[] key, int seconds, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.setex(key, seconds, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return setex(key, seconds, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public byte[] get(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.get(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return get(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long del(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.del(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return del(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long del(byte[]... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.del(keys);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return del(keys);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long expire(byte[] key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.expire(key, seconds);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return expire(key, seconds);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long ttl(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.ttl(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return ttl(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public boolean exists(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.exists(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return exists(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long incr(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.incr(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return incr(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long incrBy(byte[] key, long increment) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.incrBy(key, increment);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return incrBy(key, increment);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long decr(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.decr(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return decr(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long decrBy(byte[] key, long decrement) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.decrBy(key, decrement);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return decrBy(key, decrement);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long lpush(byte[] key, byte[]... strings) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lpush(key, strings);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return lpush(key, strings);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long rpush(byte[] key, byte[]... strings) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.rpush(key, strings);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return rpush(key, strings);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long llen(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.llen(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return llen(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public byte[] lpop(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lpop(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return lpop(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public byte[] rpop(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.rpop(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return rpop(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public List<byte[]> lrange(byte[] key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lrange(key, start, end);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return lrange(key, start, end);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public List<byte[]> lrangeAll(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lrange(key, 0, -1);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return lrangeAll(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long hset(byte[] key, byte[] field, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hset(key, field, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hset(key, field, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long hsetnx(byte[] key, byte[] field, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hsetnx(key, field, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hsetnx(key, field, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public String hmset(byte[] key, Map<byte[], byte[]> hash) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hmset(key, hash);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hmset(key, hash);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public byte[] hget(byte[] key, byte[] field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hget(key, field);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hget(key, field);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public List<byte[]> hmget(final byte[] key, final byte[]... fields) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hmget(key, fields);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hmget(key, fields);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Boolean hexists(byte[] key, byte[] field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hexists(key, field);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hexists(key, field);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long hdel(byte[] key, byte[]... fields) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hdel(key, fields);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hdel(key, fields);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long hlen(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hlen(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hlen(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Map<byte[], byte[]> hgetAll(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hgetAll(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return hgetAll(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long sadd(byte[] key, byte[]... members) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sadd(key, members);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return sadd(key, members);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Set<byte[]> smembers(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.smembers(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return smembers(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long srem(byte[] key, byte[]... members) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.srem(key, members);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return srem(key, members);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long scard(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.scard(key);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return scard(key);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Set<byte[]> sunion(byte[]... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sunion(keys);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return sunion(keys);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Set<byte[]> sdiff(byte[]... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sdiff(keys);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return sdiff(keys);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    public Long sdiffstore(byte[] dstkey, byte[]... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sdiffstore(dstkey, keys);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return sdiffstore(dstkey, keys);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    @Override
    public Long lrem(String key, long count, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lrem(key, count, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return lrem(key, count, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }

    @Override
    public Long lrem(byte[] key, long count, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lrem(key, count, value);
        } catch (JedisConnectionException jedisConnectionException) {
            createPool();
            if (canConnection()) {
                return lrem(key, count, value);
            } else {
                log.error(jedisConnectionException.getMessage(), jedisConnectionException);
                throw new CacheClientException(jedisConnectionException);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CacheClientException(e);
        } finally {
            if (jedis != null)
                returnResource(jedis);
        }
    }
}
