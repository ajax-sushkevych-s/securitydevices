package com.sushkevych.securitydevices.devicestatus.infrastructure.repository.mongo

import com.sushkevych.securitydevices.devicestatus.infrastructure.repository.entity.MongoDeviceStatus
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface DeviceStatusRepositoryOutPort : ReactiveMongoRepository<MongoDeviceStatus, ObjectId> {
    fun findByUserDeviceId(deviceId: String): Mono<MongoDeviceStatus>
}
