package com.sushkevych.securitydevices.device.infrastructure.adapters.redis.configuration

import com.sushkevych.securitydevices.device.infrastructure.repository.entity.MongoDevice
import com.sushkevych.securitydevices.core.infrastructure.configuration.redis.CoreRedisConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate

@Configuration
class RedisConfiguration : CoreRedisConfiguration<MongoDevice>(clazz = MongoDevice::class.java) {
    @Bean
    fun reactiveRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, MongoDevice> = createReactiveRedisTemplate(clazz, connectionFactory)
}
