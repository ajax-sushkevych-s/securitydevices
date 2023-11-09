package com.sushkevych.securitydevices.devicestatus.infrastructure.repository.mongo

import com.sushkevych.securitydevices.devicestatus.application.port.DeviceStatusRepositoryOutPort
import com.sushkevych.securitydevices.devicestatus.infrastructure.repository.entity.MongoDeviceStatus
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface MongoDeviceStatusRepository : DeviceStatusRepositoryOutPort,
    ReactiveMongoRepository<MongoDeviceStatus, String> {
    fun findByUserDeviceId(deviceId: String): Mono<MongoDeviceStatus>
}
