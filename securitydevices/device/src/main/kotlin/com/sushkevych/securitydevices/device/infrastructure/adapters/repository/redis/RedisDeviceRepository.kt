package com.sushkevych.securitydevices.device.infrastructure.adapters.repository.redis

import com.sushkevych.securitydevices.device.application.port.DeviceCacheableRepositoryOutPort
import com.sushkevych.securitydevices.device.application.port.DeviceRepositoryOutPort
import com.sushkevych.securitydevices.device.domain.Device
import com.sushkevych.securitydevices.device.infrastructure.mapper.toDevice
import com.sushkevych.securitydevices.device.infrastructure.mapper.toMongoDevice
import com.sushkevych.securitydevices.device.infrastructure.adapters.repository.entity.MongoDevice
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.switchIfEmptyDeferred
import java.time.Duration

@Repository
class RedisDeviceRepository(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, MongoDevice>,
    private val deviceRepository: DeviceRepositoryOutPort,
    @Value("\${spring.data.redis.ttl.minutes}") private val redisTtlMinutes: String,
    @Value("\${spring.data.redis.key.prefix}") private val deviceKeyPrefix: String
) : DeviceCacheableRepositoryOutPort {
    override fun getById(id: String): Mono<Device> =
        reactiveRedisTemplate
            .opsForValue()
            .get(deviceKeyPrefix + id)
            .map { it.toDevice() }
            .switchIfEmpty {
                deviceRepository.getById(id)
                    .flatMap {
                        saveDeviceToCache(it)
                    }
            }

    override fun findAll(): Flux<Device> = reactiveRedisTemplate.scan(
        ScanOptions
            .scanOptions()
            .match("${deviceKeyPrefix}*")
            .build()
    ).flatMap { deviceId ->
        reactiveRedisTemplate.opsForValue().get(deviceId)
            .map { it.toDevice() }
    }
        .switchIfEmptyDeferred {
            deviceRepository.findAll()
                .flatMap {
                    saveDeviceToCache(it)
                }
        }

    override fun save(entity: Device): Mono<Device> = deviceRepository.save(entity)
        .flatMap { savedDevice ->
            reactiveRedisTemplate.opsForValue()
                .set(
                    deviceKeyPrefix + savedDevice.id,
                    savedDevice.toMongoDevice(),
                    Duration.ofMinutes(redisTtlMinutes.toLong())
                )
                .thenReturn(savedDevice)
        }

    override fun update(entity: Device): Mono<Device> =
        deviceRepository.update(entity).flatMap { saveDeviceToCache(entity) }

    override fun deleteById(id: String): Mono<Unit> =
        reactiveRedisTemplate
            .delete(deviceKeyPrefix + id)
            .then(deviceRepository.deleteById(id))

    private fun saveDeviceToCache(device: Device): Mono<Device> {
        val key = device.id
        return if (key != null) {
            reactiveRedisTemplate.opsForValue()
                .set(
                    deviceKeyPrefix + key,
                    device.toMongoDevice(),
                    Duration.ofMinutes(redisTtlMinutes.toLong())
                ).thenReturn(device)
        } else {
            Mono.empty()
        }
    }
}
