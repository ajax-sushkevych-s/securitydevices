package com.sushkevych.securitydevices.device.application.services

import com.sushkevych.securitydevices.device.application.port.DeviceCacheableRepositoryOutPort
import com.sushkevych.securitydevices.device.application.port.DeviceOperationsInPort
import com.sushkevych.securitydevices.device.domain.Device
import com.sushkevych.securitydevices.core.application.exception.NotFoundException
import com.sushkevych.securitydevices.core.shared.doMonoOnNext
import com.sushkevych.securitydevices.device.application.port.DeviceEventProducerOutPort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DeviceService(
    private val deviceCacheableRepository: DeviceCacheableRepositoryOutPort,
    private val deviceEventProducer: DeviceEventProducerOutPort
) : DeviceOperationsInPort {
    override fun getById(id: String): Mono<Device> =
        deviceCacheableRepository.getById(id)
            .switchIfEmpty(Mono.error(NotFoundException(message = "Device with ID $id not found")))

    override fun findAll(): Flux<Device> = deviceCacheableRepository.findAll()

    override fun save(entity: Device): Mono<Device> = deviceCacheableRepository.save(entity)

    override fun update(entity: Device): Mono<Device> =
        deviceCacheableRepository.update(entity)
            .doMonoOnNext { deviceEventProducer.sendDeviceUpdatedEvent(it) }
            .switchIfEmpty(Mono.error(NotFoundException(message = "Device with ID ${entity.id} not found")))

    override fun delete(id: String) =
        deviceCacheableRepository.deleteById(id)
            .thenReturn(Unit)
}
