package com.livesports.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Spring Redis bean configurations.
 *
 */
@Configuration
@PropertySource("classpath:config.properties")
public class SpringRedisConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringRedisConfig.class);
	
	/** cluster mode constant. */
	private static final String CLUSTER_MODE = "cluster";

	/** spring redis key. */
	private static final String SPRING_CLUSTER_KEY = "spring.redis.cluster.nodes";

	/** redis server host name. */
	@Value("${redis.hostname:localhost}")
	private String redisHost;

	/** redis server port number. */
	@Value("${redis.port:6379}")
	private Integer redisPort;

	/** specifies clustered or standalone mode. */
	@Value("${redis.mode:standalone}")
	private String redisMode;

	/** comma seperated redis cluster nodes. hostname:port format. */
	@Value("${redis.cluster.nodes:127.0.0.1:7000}")
	private String redisClusterNodes;

	/**
	 * JedisconnectionFactory bean.
	 * 
	 * @return the jedis connection factory
	 */
	@Bean
	public JedisConnectionFactory connectionFactory() {

		if (CLUSTER_MODE.equalsIgnoreCase(redisMode)) {
			LOGGER.debug("Initialized redis in cluster mode");
			Map<String, Object> clusterNodesMap = new HashMap<>();
			clusterNodesMap.put(SPRING_CLUSTER_KEY, redisClusterNodes);

			MapPropertySource propertySource = new MapPropertySource(
					"redisClusterConfig", clusterNodesMap);

			RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(
					propertySource);

			return new JedisConnectionFactory(clusterConfig);

		} else {
			
			LOGGER.debug("Initialized redis in standalone mode");
			JedisConnectionFactory standaloneConnectionFactory = new JedisConnectionFactory();
			standaloneConnectionFactory.setHostName(redisHost);
			standaloneConnectionFactory.setPort(redisPort);
			standaloneConnectionFactory.setUsePool(true);
			return standaloneConnectionFactory;
		}
	}

	/**
	 * The redis template bean.
	 * 
	 * @return the string redis template
	 */
	@Bean
	public StringRedisTemplate redisTemplate() {
		StringRedisTemplate redisTemplate = new StringRedisTemplate();
		redisTemplate.setConnectionFactory(connectionFactory());
		return redisTemplate;
	}
}
