/*
package com.featureflag.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
public class RedisConfig {

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

	    ObjectMapper mapper = new ObjectMapper();

	    // 🔥 enable type info
	    mapper.activateDefaultTyping(
	            mapper.getPolymorphicTypeValidator(),
	            ObjectMapper.DefaultTyping.NON_FINAL
	    );

	    GenericJackson2JsonRedisSerializer serializer =
	            new GenericJackson2JsonRedisSerializer(mapper);

	    RedisSerializationContext.SerializationPair<Object> pair =
	            RedisSerializationContext.SerializationPair.fromSerializer(serializer);

	    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
	            .serializeValuesWith(pair)
	            .entryTtl(Duration.ofMinutes(10))
	            .disableCachingNullValues();

	    return RedisCacheManager.builder(connectionFactory)
	            .cacheDefaults(config)
	            .build();
	}
}*/

package com.featureflag.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
public class RedisConfig {

    // ✅ Cache Manager (for @Cacheable)
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        RedisSerializationContext.SerializationPair<Object> serializer =
                RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()
                );

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(serializer)
                .entryTtl(Duration.ofMinutes(10));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    // 🔥 IMPORTANT — RedisTemplate (FIX YOUR ERROR)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        // Key serializer
        template.setKeySerializer(new StringRedisSerializer());

        // Value serializer (JSON)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Hash key/value
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();

        return template;
    }
}

