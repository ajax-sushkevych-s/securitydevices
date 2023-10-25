package com.sushkevych.securitydevices.repository.implementation

import com.sushkevych.securitydevices.model.MongoDevice
import com.sushkevych.securitydevices.repository.DeviceCacheableRepository
import com.sushkevych.securitydevices.repository.DeviceRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Repository
class DeviceRedisRepositoryImpl(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, MongoDevice>,
    private val deviceRepository: DeviceRepository
) : DeviceCacheableRepository {

    @Value("\${redis.ttl.minutes}")
    private lateinit var redisTtlMinutes: String

    @Value("\${redis.key.prefix}")
    private lateinit var deviceKeyPrefix: String

    override fun getDeviceById(deviceId: ObjectId): Mono<MongoDevice> =
        reactiveRedisTemplate
            .opsForValue()
            .get(deviceKeyPrefix + deviceId.toHexString())
            .switchIfEmpty {
                deviceRepository.getDeviceById(deviceId)
                    .flatMap { device ->
                        saveDeviceToCache(device)
                    }
            }

    override fun findAll(): Mono<List<MongoDevice>> =
        reactiveRedisTemplate.scan(ScanOptions.scanOptions().match("${deviceKeyPrefix}*").build())
            .flatMap { reactiveRedisTemplate.opsForValue().get(it) }
            .collectList()
            .flatMap { cachedDevices ->
                if (cachedDevices.isNotEmpty()) {
                    cachedDevices.toMono()
                } else {
                    deviceRepository.findAll()
                        .flatMap { saveDeviceToCache(it) }
                        .collectList()
                }
            }

    override fun save(device: MongoDevice): Mono<MongoDevice> = deviceRepository.save(device)
        .flatMap { savedDevice ->
            reactiveRedisTemplate.opsForValue()
                .set(
                    deviceKeyPrefix + savedDevice.id,
                    savedDevice,
                    Duration.ofMinutes(redisTtlMinutes.toLong())
                )
                .then(savedDevice.toMono())
        }

    override fun update(device: MongoDevice): Mono<MongoDevice> =
        deviceRepository.update(device).flatMap { saveDeviceToCache(device) }

    override fun deleteById(deviceId: ObjectId): Mono<Unit> =
        reactiveRedisTemplate
            .delete(deviceKeyPrefix + deviceId.toHexString())
            .then(deviceRepository.deleteById(deviceId))

    private fun saveDeviceToCache(device: MongoDevice): Mono<MongoDevice> {
        val key = device.id?.toHexString()
        return if (key != null) {
            reactiveRedisTemplate.opsForValue()
                .set(
                    deviceKeyPrefix + key,
                    device,
                    Duration.ofMinutes(redisTtlMinutes.toLong())
                ).thenReturn(device)
        } else {
            Mono.empty()
        }
    }
}
