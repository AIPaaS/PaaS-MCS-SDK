package com.ai.paas.ipaas.mcs.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.LoggerFactory;

import com.ai.paas.ipaas.mcs.exception.CacheClientException;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.util.Assert;
import com.ai.paas.ipaas.util.StringUtil;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

public class CacheCodisClient implements ICacheClient {

	private static transient final org.slf4j.Logger log = LoggerFactory.getLogger(CacheCodisClient.class);

	JedisResourcePool jedisPool = null;

	String zkAddr = null;
	String codisPath = null;
	// 2.0+/zk/codis/db_xxx/proxy
	// 3.0+/jodis/xxx
	private Jedis jedis = null;

	public CacheCodisClient(String zkAddr, String coidsPath) {
		this.zkAddr = zkAddr;
		this.codisPath = coidsPath;
		createPool();
	}

	private synchronized void createPool() {
		jedisPool = RoundRobinJedisPool.create().curatorClient(zkAddr, 30000).zkProxyDir(codisPath).build();
	}

	/**
	 * redis是否可用
	 *
	 * @return
	 */
	private boolean canConnection() {
		if (jedisPool == null)
			return false;

		try {
			jedis = getJedis();
			jedis.connect();
			jedis.get("ok");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {

		}
		return true;
	}

	private Jedis getJedis() {
		if (null != jedis)
			return jedis;
		return jedisPool.getResource();
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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

		}
	}

	@Override
	public Long expireAt(String key, long seconds) {

		try {
			jedis = getJedis();
			return jedis.expireAt((key), seconds);
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
		} finally {

		}
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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

		}
	}

	@Override
	public String acquireLock(String lockName, long acquireTimeoutInMS, long lockTimeoutInMS) {

		String retIdentifier = null;
		try {
			jedis = getJedis();
			String identifier = UUID.randomUUID().toString();
			String lockKey = "lock:" + lockName;
			int lockExpire = (int) (lockTimeoutInMS / 1000);

			long end = System.currentTimeMillis() + acquireTimeoutInMS;
			while (System.currentTimeMillis() < end) {
				if (jedis.setnx(lockKey, identifier) == 1) {
					jedis.expire(lockKey, lockExpire);
					retIdentifier = identifier;
				}
				if (jedis.ttl(lockKey) == -1) {
					jedis.expire(lockKey, lockExpire);
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (JedisConnectionException jedisConnectionException) {
			log.error(jedisConnectionException.getMessage(), jedisConnectionException);
			throw new CacheClientException(jedisConnectionException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {

		}

		return retIdentifier;
	}

	@Override
	public boolean releaseLock(String lockName, String identifier) {

		String lockKey = "lock:" + lockName;
		boolean retFlag = false;
		try {
			jedis = getJedis();
			while (true) {
				jedis.watch(lockKey);
				if (identifier.equals(jedis.get(lockKey))) {
					Transaction trans = jedis.multi();
					trans.del(lockKey);
					List<Object> results = trans.exec();
					if (results == null) {
						continue;
					}
					retFlag = true;
				}
				jedis.unwatch();
				break;
			}
		} catch (JedisConnectionException jedisConnectionException) {
			log.error(jedisConnectionException.getMessage(), jedisConnectionException);
			throw new CacheClientException(jedisConnectionException);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CacheClientException(e);
		} finally {

		}
		return retFlag;
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
		} finally {

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
		} finally {

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
		} finally {

		}
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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

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
		} finally {

		}
	}

	@Override
	public void close() {
		if (null != jedis) {
			jedis.close();
		}
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
}
