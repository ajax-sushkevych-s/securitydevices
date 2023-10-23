package com.sushkevych.securitydevices.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.sushkevych.securitydevices.model.MongoDevice
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableCaching
class RedisConfiguration {

    @Value("\${redis.host}")
    lateinit var host: String

    @Value("\${redis.port}")
    lateinit var port: String

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val config = RedisStandaloneConfiguration(host, port.toInt())
        val factory = LettuceConnectionFactory(config)
        factory.afterPropertiesSet()
        return factory
    }

    @Bean
    fun reactiveRedisTemplate(
        @Qualifier("reactiveRedisConnectionFactory") connectionFactory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, MongoDevice> {
        val objectMapper = ObjectMapper().findAndRegisterModules()
        val serializer = Jackson2JsonRedisSerializer(objectMapper, MongoDevice::class.java)
        val context = RedisSerializationContext
            .newSerializationContext<String, MongoDevice>(StringRedisSerializer())
            .value(serializer)
            .build()
        return ReactiveRedisTemplate(connectionFactory, context)
    }
}

