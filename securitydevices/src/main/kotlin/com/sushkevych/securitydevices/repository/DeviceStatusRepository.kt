package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoDeviceStatus
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceStatusRepository : MongoRepository<MongoDeviceStatus, ObjectId> {
    fun findByUserDeviceId(deviceId: String) : MongoDeviceStatus?
}
