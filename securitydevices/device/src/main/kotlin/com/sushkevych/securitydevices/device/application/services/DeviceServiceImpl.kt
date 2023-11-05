package com.sushkevych.securitydevices.device.application.services

import com.sushkevych.securitydevices.device.application.port.DeviceCacheableRepository
import com.sushkevych.securitydevices.device.application.port.DeviceService
import com.sushkevych.securitydevices.device.domain.Device
import com.sushkevych.securitydevices.device.infrastructure.adapters.kafka.producer.DeviceKafkaProducer
import com.sushkevych.securitydevices.core.application.exception.NotFoundException
import com.sushkevych.securitydevices.core.shared.doMonoOnNext
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DeviceServiceImpl(
    private val deviceCacheableRepository: DeviceCacheableRepository,
    private val deviceKafkaProducer: DeviceKafkaProducer
) : DeviceService {
    override fun getById(id: String): Mono<Device> =
        deviceCacheableRepository.getById(id)
            .switchIfEmpty(Mono.error(NotFoundException(message = "Device with ID $id not found")))

    override fun findAll(): Flux<Device> = deviceCacheableRepository.findAll()

    override fun save(entity: Device): Mono<Device> = deviceCacheableRepository.save(entity)

    override fun update(entity: Device): Mono<Device> =
        deviceCacheableRepository.update(entity)
            .doMonoOnNext { deviceKafkaProducer.sendDeviceUpdatedEventToKafka(it) }
            .switchIfEmpty(Mono.error(NotFoundException(message = "Device with ID ${entity.id} not found")))

    override fun delete(id: String) =
        deviceCacheableRepository.deleteById(id)
            .thenReturn(Unit)
}
