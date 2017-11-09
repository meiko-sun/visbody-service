package cn.lazy.base;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cn.lazy.model.AccessToken;
import cn.lazy.utils.JSONUtil;


/**
 * 	redis基类
  * @类名: RedisService
  * @描述: TODO .
  * @程序猿: sundefa .
  * @日期: 2017年10月28日 下午5:53:47
  * @版本号: V2.0 .
  *
 */

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, ?> redisTemplate;

	/**
	 * 
	  * @方法名: set
	  * @描述: 根据模板设置键值 .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:54:08
	  * @返回值: boolean  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public boolean set(final String key, final String value) {
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				connection.set(serializer.serialize(key), serializer.serialize(value));
				connection.close();
				connection.closePipeline();
				return true;
			}
		});
		return result;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean setSqlCacheStr(final byte[] key, final byte[] value) {
		// ValueOperations opsForValue = redisTemplate.opsForValue();
		// opsForValue.set(key, value, ConstantUtil.EXPIRE_TIME_IN_MINUTES,
		// TimeUnit.MINUTES);
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				connection.select(1);
				connection.set(key, value);
				connection.close();
				connection.closePipeline();
				return true;
			}
		});
		return result;
	}

	public byte[] getSqlCacheStr(final byte[] key) {
		// ValueOperations opsForValue = redisTemplate.opsForValue();
		// return opsForValue.get(key);
		byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
			public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
				connection.select(1);
				byte[] result = connection.get(key);
				connection.close();
				connection.closePipeline();
				return result;
			}
		});
		return result;
	}

	public boolean deleteSqlCacheStr(final String key) {
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				connection.select(1);
				Long num = connection.del(serializer.serialize(key));
				connection.close();
				connection.closePipeline();
				if (num > 0)
					return true;
				else
					return false;
			}

		});
		return result;
	}

	/**
	 * 
	  * @方法名: clearSqlCache
	  * @描述: 清空数据库----针对sql缓存 .  .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:54:33
	  * @返回值: void  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public void clearSqlCache() {
		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
		connection.select(1);
		connection.flushDb();
		connection.close();
		connection.closePipeline();
	}

	/**
	 * 
	  * @方法名: dbSizeSqlCache
	  * @描述: 数据库长度----针对sql缓存  .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:54:48
	  * @返回值: Long  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public Long dbSizeSqlCache() {
		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
		connection.select(1);
		long size = connection.dbSize();
		connection.close();
		connection.closePipeline();
		return size;
	}

	/**
	 * 
	  * @方法名: get
	  * @描述: 根据键获取相对应的值 .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:55:02
	  * @返回值: String  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public String get(final String key) {
		String result = redisTemplate.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				byte[] value = connection.get(serializer.serialize(key));
				connection.close();
				connection.closePipeline();
				return serializer.deserialize(value);
			}
		});
		return result;
	}

	public void delete(final String key) {
        redisTemplate.delete(key);
    }

	/**
	 * 
	  * @方法名: expire
	  * @描述: 设置键的到期时间  .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:55:16
	  * @返回值: boolean  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public boolean expire(final String key, long expire) {
		return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
	}

	public boolean putAccessToken(String tableKey, AccessToken accessToken) {
		redisTemplate.opsForHash().put(tableKey, String.valueOf(accessToken.getId()), JSONUtil.toJson(accessToken));
		return this.expire(tableKey, accessToken.getExpiresIn());
	}

	/**
	 * 
	  * @方法名: expire
	  * @描述: 查询指定的key是否存在 .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:55:30
	  * @返回值: boolean  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public boolean expire(final String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 
	  * @方法名: expireAt
	  * @描述: 设置键的到期时间(按日期） .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:55:42
	  * @返回值: boolean  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public boolean expireAt(final String key, Date date) {
		boolean status = false;
		if (isExpire(key) == -1) {
			status = redisTemplate.expireAt(key, date);
		}
		return status;
	}

	/**
	 * 
	  * @方法名: isExpire
	  * @描述: 查看指定的键是否设置过期时间,存在获取过期时间  .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:55:55
	  * @返回值: long  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public long isExpire(final String key) {
		return redisTemplate.getExpire(key);
	}

	/**
	 * 
	  * @方法名: getExpireDate
	  * @描述: 根据key获取过期时间并换算成指定单位  .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:56:08
	  * @返回值: long  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public long getExpireDate(final String key) {
		return redisTemplate.getExpire(key, TimeUnit.MICROSECONDS);
	}
	/**
	 * 
	  * @方法名: setList
	  * @描述:  设置list .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:56:20
	  * @返回值: boolean  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public <T> boolean setList(String key, List<T> list) {
		String value = JSONUtil.toJson(list);
		return set(key, value);
	}
	/**
	 * 
	  * @方法名: getList
	  * @描述: 获取集合 .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:56:33
	  * @返回值: List<T>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public <T> List<T> getList(String key, Class<T> clz) {
		String json = get(key);
		if (json != null) {
			List<T> list = JSONUtil.toList(json, clz);
			return list;
		}
		return null;
	}

	/**
	 * 
	  * @方法名: lpush
	  * @描述: 从左边将一个或多个值插入到列表头部 .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午5:57:20
	  * @返回值: long  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public long lpush(final String key, Object obj) {
		final String value = JSONUtil.toJson(obj);
		long result = redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				long count = connection.lPush(serializer.serialize(key), serializer.serialize(value));
				connection.close();
				connection.closePipeline();
				return count;
			}
		});
		return result;
	}

	/**
	 * 
	  * @方法名: rpush
	  * @描述: 从右边将一个或多个值插入到列表头部  .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午6:05:30
	  * @返回值: long  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public long rpush(final String key, Object obj) {
		final String value = JSONUtil.toJson(obj);
		long result = redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				long count = connection.rPush(serializer.serialize(key), serializer.serialize(value));
				connection.close();
				connection.closePipeline();
				return count;
			}
		});
		return result;
	}

	/**
	 * 
	  * @方法名: lpop
	  * @描述: 删除，并返回保存列表在key的第一个元素 .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午6:05:46
	  * @返回值: String  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public String lpop(final String key) {
		String result = redisTemplate.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				byte[] res = connection.lPop(serializer.serialize(key));
				connection.close();
				connection.closePipeline();
				return serializer.deserialize(res);
			}
		});
		return result;
	}

	
	public AccessToken findAccessToken(String tableKey, AccessToken tokenKey) {
		String json = (String) redisTemplate.opsForHash().get(tableKey, String.valueOf(tokenKey.getId()));
		return JSONUtil.toBean(json, AccessToken.class);
	}
	

	public void boundHashOps(String tableName) {
		BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(tableName);
		redisTemplate.delete("student");
		redisTemplate.delete("student:1");
		ops.put("cs01", "123");// 存入数据 ops.putAll(maps); 存入多条数据
		String key1 = ops.getKey();// tableName的名字
		System.out.println("key1:" + key1);
		String key11 = ops.get("cs01");
		System.out.println("key11:" + key11);// 获取key的值

		ops.putIfAbsent("cs02", "456");
		String key2 = ops.getKey();
		System.out.println("ops.getKey()-key2:" + key2);
		String key21 = ops.get("cs02");
		System.out.println("ops.get(cs02)-key21:" + key21);

		Map<String, String> maps = ops.entries();// 获取所有的key-value值
		for (String key : maps.keySet()) {
			System.out.println("map-key:" + key + "map-value:" + maps.get(key));
		}
		// ops.persist();//删除过期(如果有的话)的数据。
		System.out.println("ops.getExpire():" + ops.getExpire());// -1
		System.out.println("ops.expireAt(new Date()):" + ops.expireAt(new Date()));// true 设置生存过期时间
		System.out.println("ops.getType():" + ops.getType());// Hash
		System.out.println("ops.hasKey(cs01):" + ops.hasKey("cs01"));// true
		System.out.println("ops.hasKey(cs02):" + ops.hasKey("cs02"));// true
		System.out.println("ops.size():" + ops.size());// 2

		Set<String> keys = ops.keys();// 获取所有的key
		for (String string : keys) {
			System.out.println("ops.keys():" + string);
		}

		System.out.println("ops.values():" + ops.values());// 获取所有的value
		System.out.println("ops.size():" + ops.size());// 2 获取数量

		ops.delete("cs01");// 删除key为cs01的数据
	}

	/**
	 * 
	  * @方法名: isBoundForHash
	  * @描述: 未绑定hashtable的名字 .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午6:06:11
	  * @返回值: boolean  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public boolean isBoundForHash(String tableName, String json) {
		HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
		JSONObject jsonObject = JSONObject.parseObject(json);
		String mobile = jsonObject.getString("mobile");
		String verifCode = jsonObject.getString("card");
		Map<Object, Object> maps = ops.entries(tableName);// 获取所有的key-value值
		for (Object key : maps.keySet()) {
			if (maps.get(key).equals(mobile)) {
				return false;
			}
			// System.out.println("map-key:" + key + "map-value:" + maps.get(key));
		}
		ops.put(tableName, mobile, verifCode);// 存入数据 ops.putAll(maps); 存入多条数据
		return true;
	}

	/**
	 * 未指定hashtable的名字
	 * 
	 * @param tableName
	 */
	public void opsForHash(String tableName) {
		System.out.println("==================Hash==============");
		HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
		redisTemplate.delete("student");
		redisTemplate.delete("student:1");
		ops.put(tableName, "cs01", "123");// 存入数据 ops.putAll(maps); 存入多条数据
		Object key11 = ops.get(tableName, "cs01");
		System.out.println("key11:" + key11);// 获取key的值

		ops.putIfAbsent(tableName, "cs02", "456");
		Object key21 = ops.get(tableName, "cs02");
		System.out.println("ops.get(cs02)-key21:" + key21);

		Map<Object, Object> maps = ops.entries(tableName);// 获取所有的key-value值
		for (Object key : maps.keySet()) {
			System.out.println("map-key:" + key + "map-value:" + maps.get(key));
		}
		// ops.persist();//删除过期(如果有的话)的数据。
		System.out.println("ops.hasKey(cs01):" + ops.hasKey(tableName, "cs01"));// true
		System.out.println("ops.hasKey(cs02):" + ops.hasKey(tableName, "cs02"));// true
		System.out.println("ops.size():" + ops.size(tableName));// 2

		Set<Object> keys = ops.keys(tableName);// 获取所有的key
		for (Object string : keys) {
			System.out.println("ops.keys():" + string);
		}

		System.out.println("ops.values():" + ops.values(tableName));// 获取所有的value
		System.out.println("ops.size():" + ops.size(tableName));// 2 获取数量

		ops.delete("cs01");// 删除key为cs01的数据
	}

}
