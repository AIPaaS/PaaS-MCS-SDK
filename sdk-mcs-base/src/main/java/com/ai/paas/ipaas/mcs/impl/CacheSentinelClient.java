package com.ai.paas.ipaas.mcs.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

import com.ai.paas.ipaas.mcs.exception.CacheClientException;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.util.Assert;
import com.ai.paas.ipaas.util.StringUtil;

public class CacheSentinelClient implements ICacheClient {

	private static transient final org.slf4j.Logger log = LoggerFactory.getLogger(CacheClient.class);
	private JedisSentinelPool pool;
	private GenericObjectPoolConfig config;
	private static final int TIMEOUT_KEY = 15000;
	private String host;
	@SuppressWarnings("unused")
	private final String preKey = CacheHelper.preKey();
	private String pwd;
	@SuppressWarnings("unused")
	private boolean isRedisNeedAuth = false;
	private Jedis jedis = null;

	public CacheSentinelClient(GenericObjectPoolConfig config, String host) {
		this.config = config;
		this.host = host;
		createPool();
	}

	public CacheSentinelClient(GenericObjectPoolConfig config, String host, String pwd) {
		this.config = config;
		this.host = host;
		if (!StringUtil.isBlank(pwd)) {
			this.pwd = pwd;
			isRedisNeedAuth = true;
		}
		createPool();
	}

	private synchronized void createPool() {
		if (!canConnection()) {
			log.info("Create JedisPool Begin ...");
			try {
				if (config.getMaxWaitMillis() < 20000)
					config.setMaxWaitMillis(20000);

				Set<String> sentinels = new HashSet<String>(Arrays.asList(host.split(";|,")));
				pool = new JedisSentinelPool("mymaster", sentinels, config, TIMEOUT_KEY, pwd);
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

		try {
			jedis = getJedis();
			jedis.connect();
			jedis.get("ok");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

		return true;
	}

	private Jedis getJedis() {
		if (null == jedis) {
			return pool.getResource();
		}
		return jedis;
	}

	public void destroyPool() {
		if (null != pool) {
			pool.destroy();
		}
	}

	public String set(String key, String value) {

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
		}

	}

	public String setex(String key, int seconds, String value) {

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
		}

	}

	public String get(String key) {

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
		}

	}

	public Long del(String key) {

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
		}

	}

	public Long hincrBy(String key, String field, long value) {

		try {
			jedis = getJedis();
			return jedis.hincrBy(key, field, value);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return hincrBy(key, field, value);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Double incrByFloat(String key, double value) {

		try {
			jedis = getJedis();
			return jedis.incrByFloat(key, value);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return incrByFloat(key, value);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Double hincrByFloat(String key, String field, double value) {

		try {
			jedis = getJedis();
			return jedis.hincrByFloat(key, field, value);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return hincrByFloat(key, field, value);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	public Long del(String... keys) {

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
		}

	}

	public Long expire(String key, int seconds) {

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
		}

	}

	@Override
	public Long expireAt(String key, long seconds) {
		return expireAt(key.getBytes(), seconds);
	}

	public Long ttl(String key) {

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
		}

	}

	public boolean exists(String key) {

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
		}

	}

	public Long incr(String key) {

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
		}

	}

	public Long incrBy(String key, long increment) {

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
		}

	}

	public Long decr(String key) {

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
		}

	}

	public Long decrBy(String key, long decrement) {

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
		}

	}

	public Long lpush(String key, String... strings) {

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
		}

	}

	public Long rpush(String key, String... strings) {

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
		}

	}

	public Long llen(String key) {

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
		}

	}

	public String lpop(String key) {

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
		}

	}

	public String rpop(String key) {

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
		}

	}

	public List<String> lrange(String key, long start, long end) {

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
		}

	}

	public List<String> lrangeAll(String key) {

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
		}

	}

	public Long hset(String key, String field, String value) {

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
		}

	}

	public Long hsetnx(String key, String field, String value) {

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
		}

	}

	public String hmset(String key, Map<String, String> hash) {

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
		}

	}

	public String hget(String key, String field) {

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
		}

	}

	public List<String> hmget(final String key, final String... fields) {

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
		}

	}

	public Boolean hexists(String key, String field) {

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
		}

	}

	public Long hdel(String key, String... fields) {

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
		}

	}

	public Long hlen(String key) {

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
		}

	}

	public Map<String, String> hgetAll(String key) {

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
		}

	}

	public Long sadd(String key, String... members) {

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
		}

	}

	public Set<String> smembers(String key) {

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
		}

	}

	public Long srem(String key, String... members) {

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
		}

	}

	public Long scard(String key) {

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
		}

	}

	public Set<String> sunion(String... keys) {

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
		}

	}

	public Set<String> sdiff(String... keys) {

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
		}

	}

	public Long sdiffstore(String dstkey, String... keys) {

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
		}

	}

	public String set(byte[] key, byte[] value) {

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
		}

	}

	public String setex(byte[] key, int seconds, byte[] value) {

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
		}

	}

	public byte[] get(byte[] key) {

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
		}

	}

	public Long del(byte[] key) {

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
		}

	}

	public Long del(byte[]... keys) {

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
		}

	}

	public Long expire(byte[] key, int seconds) {

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
		}

	}

	@Override
	public Long expireAt(byte[] key, long seconds) {

		try {
			jedis = getJedis();
			return jedis.expireAt(key, seconds);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return expireAt(key, seconds);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	public Long ttl(byte[] key) {

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
		}

	}

	public boolean exists(byte[] key) {

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
		}

	}

	public Long incr(byte[] key) {

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
		}

	}

	public Long incrBy(byte[] key, long increment) {

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
		}

	}

	public Long decr(byte[] key) {

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
		}

	}

	public Long decrBy(byte[] key, long decrement) {

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
		}

	}

	public Long lpush(byte[] key, byte[]... strings) {

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
		}

	}

	public Long rpush(byte[] key, byte[]... strings) {

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
		}

	}

	public Long llen(byte[] key) {

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
		}

	}

	public byte[] lpop(byte[] key) {

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
		}

	}

	public byte[] rpop(byte[] key) {

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
		}

	}

	public List<byte[]> lrange(byte[] key, long start, long end) {

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
		}

	}

	public List<byte[]> lrangeAll(byte[] key) {

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
		}

	}

	public Long hset(byte[] key, byte[] field, byte[] value) {

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
		}

	}

	public Long hsetnx(byte[] key, byte[] field, byte[] value) {

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
		}

	}

	@Override
	public Long setnx(byte[] key, byte[] value) {

		try {
			jedis = getJedis();
			return jedis.setnx(key, value);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return jedis.setnx(key, value);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long setnx(String key, String value) {
		return setnx(key.getBytes(), value.getBytes());
	}

	public String hmset(byte[] key, Map<byte[], byte[]> hash) {

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
		}

	}

	public byte[] hget(byte[] key, byte[] field) {

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
		}

	}

	public List<byte[]> hmget(final byte[] key, final byte[]... fields) {

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
		}

	}

	public Boolean hexists(byte[] key, byte[] field) {

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
		}

	}

	public Long hdel(byte[] key, byte[]... fields) {

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
		}

	}

	public Long hlen(byte[] key) {

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
		}

	}

	public Map<byte[], byte[]> hgetAll(byte[] key) {

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
		}

	}

	public Long sadd(byte[] key, byte[]... members) {

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
		}

	}

	public Set<byte[]> smembers(byte[] key) {

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
		}

	}

	public Long srem(byte[] key, byte[]... members) {

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
		}

	}

	public Long scard(byte[] key) {

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
		}

	}

	public Set<byte[]> sunion(byte[]... keys) {

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
		}

	}

	public Set<byte[]> sdiff(byte[]... keys) {

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
		}

	}

	public Long sdiffstore(byte[] dstkey, byte[]... keys) {

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
		}

	}

	@Override
	public Long lrem(String key, long count, String value) {

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
		}

	}

	@Override
	public Long lrem(byte[] key, long count, byte[] value) {

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
		}

	}

	@Override
	public Long zadd(String key, double score, String member) {

		try {
			jedis = getJedis();
			return jedis.zadd(key, score, member);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zadd(key, score, member);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long zadd(final String key, final double score, final String member, final ZAddParams params) {

		try {
			jedis = getJedis();
			return jedis.zadd(key, score, member, params);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zadd(key, score, member, params);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {

		try {
			jedis = getJedis();
			return jedis.zadd(key, scoreMembers);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zadd(key, scoreMembers);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long zadd(final String key, final Map<String, Double> scoreMembers, final ZAddParams params) {

		try {
			jedis = getJedis();
			return jedis.zadd(key, scoreMembers, params);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zadd(key, scoreMembers, params);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long zcount(final String key, final double min, final double max) {

		try {
			jedis = getJedis();
			return jedis.zcount(key, min, max);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zcount(key, min, max);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long zcount(final String key, final String min, final String max) {

		try {
			jedis = getJedis();
			return jedis.zcount(key, min, max);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zcount(key, min, max);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Double zincrby(final String key, final double score, final String member) {

		try {
			jedis = getJedis();
			return jedis.zincrby(key, score, member);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zincrby(key, score, member);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Double zincrby(String key, double score, String member, ZIncrByParams params) {

		try {
			jedis = getJedis();
			return jedis.zincrby(key, score, member, params);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zincrby(key, score, member, params);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Set<String> zrange(final String key, final long start, final long end) {

		try {
			jedis = getJedis();
			return jedis.zrange(key, start, end);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zrange(key, start, end);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Set<String> zrangeByScore(final String key, final double min, final double max) {

		try {
			jedis = getJedis();
			return jedis.zrangeByScore(key, min, max);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zrangeByScore(key, min, max);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Set<String> zrangeByScore(final String key, final String min, final String max) {

		try {
			jedis = getJedis();
			return jedis.zrangeByScore(key, min, max);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zrangeByScore(key, min, max);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset,
			int count) {

		try {
			jedis = getJedis();
			return jedis.zrangeByScore(key, min, max, offset, count);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zrangeByScore(key, min, max, offset, count);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Set<String> zrevrange(final String key, final long start, final long end) {

		try {
			jedis = getJedis();
			return jedis.zrevrange(key, start, end);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zrevrange(key, start, end);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final double max, final double min) {

		try {
			jedis = getJedis();
			return jedis.zrevrangeByScore(key, max, min);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zrevrangeByScore(key, max, min);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final String max, final String min) {

		try {
			jedis = getJedis();
			return jedis.zrevrangeByScore(key, max, min);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zrevrangeByScore(key, max, min);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset,
			int count) {

		try {
			jedis = getJedis();
			return jedis.zrevrangeByScore(key, max, min, offset, count);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zrevrangeByScore(key, max, min, offset, count);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long zrevrank(final String key, final String member) {

		try {
			jedis = getJedis();
			return jedis.zrevrank(key, member);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zrevrank(key, member);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long zrem(final String key, final String... member) {

		try {
			jedis = getJedis();
			return jedis.zrem(key, member);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zrem(key, member);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long zremrangeByRank(final String key, final long start, final long end) {

		try {
			jedis = getJedis();
			return jedis.zremrangeByRank(key, start, end);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zremrangeByRank(key, start, end);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long zremrangeByScore(final String key, final double start, final double end) {

		try {
			jedis = getJedis();
			return jedis.zremrangeByScore(key, start, end);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zremrangeByScore(key, start, end);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long zremrangeByScore(final String key, final String start, final String end) {

		try {
			jedis = getJedis();
			return jedis.zremrangeByScore(key, start, end);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return zremrangeByScore(key, start, end);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Long publish(final String channel, final String message) {

		try {
			jedis = getJedis();
			return jedis.publish(channel, message);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return publish(channel, message);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {

		try {
			jedis = getJedis();
			jedis.subscribe(jedisPubSub, channels);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				subscribe(jedisPubSub, channels);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {

		try {
			jedis = getJedis();
			jedis.psubscribe(jedisPubSub, patterns);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				psubscribe(jedisPubSub, patterns);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public String acquireLock(String lockName, long acquireTimeoutInMS, long lockTimeoutInMS) {
		throw new CacheClientException("sentinel mode is no surpport lock.");
	}

	@Override
	public boolean releaseLock(String lockName, String identifier) {
		throw new CacheClientException("sentinel mode is no surpport lock.");
	}

	@Override
	public Set<String> hkeys(String key) {

		try {
			jedis = getJedis();
			return jedis.hkeys(key);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return hkeys(key);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public List<String> hvals(String key) {

		try {
			jedis = getJedis();
			return jedis.hvals(key);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return hvals(key);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Set<byte[]> hkeys(byte[] key) {

		try {
			jedis = getJedis();
			return jedis.hkeys(key);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return hkeys(key);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public List<byte[]> hvals(byte[] key) {

		try {
			jedis = getJedis();
			return jedis.hvals(key);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return hvals(key);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public Set<String> keys(String pattern) {

		try {
			if (StringUtil.isBlank(pattern))
				return null;
			if ("*".equals(pattern))
				return null;
			jedis = getJedis();
			return jedis.keys(pattern);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return jedis.keys(pattern);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	public static void main(String[] args) {
		ICacheClient client = new CacheSentinelClient(new GenericObjectPoolConfig(),
				"10.1.235.23:26379,10.1.235.22:26379,10.1.235.24:26379", "");
		client.set("dxf", "1234567");
		System.out.println(client.get("dxf"));
	}

	@Override
	public Transaction startTransaction() {
		try {
			jedis = getJedis();
			return jedis.multi();
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				return startTransaction();
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}
	}

	@Override
	public void commitTransaction(Transaction tx) {
		Assert.notNull(tx, "Transaction tx can not be null!");
		try {
			tx.exec();
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				commitTransaction(tx);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}
	}

	@Override
	public void rollbackTransaction(Transaction tx) {
		Assert.notNull(tx, "Transaction tx can not be null!");
		try {
			tx.discard();
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				rollbackTransaction(tx);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}
	}

	@Override
	public void watch(String[] keys) {
		try {
			jedis = getJedis();
			jedis.watch(keys);
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				watch(keys);
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}
	}

	@Override
	public void unwatch() {
		try {
			jedis = getJedis();
			jedis.unwatch();
		} catch (JedisConnectionException jedisConnectionException) {
			createPool();
			if (canConnection()) {
				unwatch();
			} else {
				log.error(jedisConnectionException.getMessage(), jedisConnectionException);
				throw new CacheClientException(jedisConnectionException);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		}

	}

	@Override
	public void close() {
		if (null != jedis) {
			jedis.close();
		}
		destroyPool();
	}
}
