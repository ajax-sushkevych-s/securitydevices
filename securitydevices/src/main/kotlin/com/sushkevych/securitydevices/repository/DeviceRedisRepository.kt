package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoDevice
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Repository
class DeviceRedisRepository(private val reactiveRedisTemplate: ReactiveRedisTemplate<String, MongoDevice>) :
    DeviceRepository {

    @Value("\${redis.ttl.minutes}")
    private lateinit var redisTtlMinutes: String

    @Value("\${redis.key.prefix}")
    private lateinit var deviceKeyPrefix: String

    override fun getDeviceById(deviceId: ObjectId): Mono<MongoDevice> =
        reactiveRedisTemplate
            .opsForValue()
            .get(deviceKeyPrefix + deviceId.toHexString())

    override fun findAll(): Flux<MongoDevice> =
        reactiveRedisTemplate.keys("${deviceKeyPrefix}*")
            .flatMap { key -> reactiveRedisTemplate.opsForValue().get(key) }

    override fun save(device: MongoDevice): Mono<MongoDevice> {
        val key = device.id?.toHexString()
        return key?.let { nonNullKey ->
            reactiveRedisTemplate.opsForValue()
                .set(
                    deviceKeyPrefix + nonNullKey,
                    device,
                    Duration.ofMinutes(redisTtlMinutes.toLong())
                )
                .thenReturn(device)
        } ?: Mono.empty()
    }

    override fun update(device: MongoDevice): Mono<MongoDevice> = save(device)

    override fun deleteById(deviceId: ObjectId): Mono<Unit> =
        reactiveRedisTemplate
            .delete(deviceKeyPrefix + deviceId.toHexString())
            .then(Unit.toMono())
}
