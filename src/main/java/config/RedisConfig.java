package cn.lazy.config;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.java.Log;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
  * @类名: RedisConfig
  * @描述: TODO .
  * @程序猿: sundefa .
  * @日期: 2017年10月28日 下午6:15:07
  * @版本号: V2.0 .
  *
 */
@Configuration
@EnableAutoConfiguration
@Log
// @EnableRedisHttpSession(maxInactiveIntervalInSeconds = 24 * 60 * 60)
public class RedisConfig {

	private int expireTime = 60 * 60;
	JedisPoolConfig config = new JedisPoolConfig();

	@Value("${spring.redis.hostName}")
	private String hostName;

	@Value("${spring.redis.port}")
	private int redisPrort;

	@Value("${spring.redis.password}")
	private String passWord;

	@Value("${spring.redis.pool.maxActive}")
	private String maxActive;

	@Value("${spring.redis.pool.maxWait}")
	private int maxWait;

	@Value("${spring.redis.pool.maxIdle}")
	private int maxIdle;

	@Value("${spring.redis.pool.minIdle}")
	private int minIdle;

	@Value("${spring.redis.timeout}")
	private int timeOut;

	@Bean
	public JedisConnectionFactory getConnectionFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName(this.hostName.trim());
		factory.setPort(this.redisPrort);
		factory.setPassword(this.passWord.trim());
		factory.setTimeout(this.timeOut);
		config.setMaxIdle(this.maxIdle);
		config.setMinIdle(this.minIdle);
		config.setMaxWaitMillis(this.maxWait);
		factory.setPoolConfig(this.config);
		log.info("JedisConnectionFactory bean init success.");
		return factory;
	}

	@SuppressWarnings("rawtypes")
	@Bean
	public CacheManager cacheManager(RedisTemplate redisTemplate) {
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
		// Number of seconds before expiration. Defaults to unlimited (0)
		cacheManager.setDefaultExpiration(expireTime); // 设置key-value超时时间
		return cacheManager;
		// return new RedisCacheManager(redisTemplate);
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
		StringRedisTemplate template = new StringRedisTemplate(factory);
		setSerializer(template); // 设置序列化工具，这样ReportBean不需要实现Serializable接口
		template.afterPropertiesSet();
		return template;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setSerializer(StringRedisTemplate template) {
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);
	}

	/**
	 * 生成key的策略
	 * 
	 * @return
	 */
	@Bean
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getName());
				sb.append(method.getName());
				for (Object obj : params) {
					sb.append(obj.toString());
				}
				return sb.toString();
			}
		};
	}
}