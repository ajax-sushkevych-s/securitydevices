package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoDevice
import com.sushkevych.securitydevices.model.MongoDeviceStatus
import com.sushkevych.securitydevices.model.MongoUser
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class DeviceQueryRepository(private val mongoTemplate: MongoTemplate) : DeviceRepository {
    override fun getDeviceById(deviceId: ObjectId): MongoDevice? =
        mongoTemplate.findOne(
            Query(Criteria.where("id").`is`(deviceId)),
            MongoDevice::class.java,
            MongoDevice.COLLECTION_NAME
        )

    override fun findAll(): List<MongoDevice> = mongoTemplate.findAll(MongoDevice::class.java)

    override fun save(device: MongoDevice): MongoDevice? = mongoTemplate.save(device, MongoDevice.COLLECTION_NAME)

    override fun deleteById(deviceId: ObjectId) {
        val deviceQuery = Query(Criteria.where("id").`is`(deviceId))

        val userDeviceIds = extractUserDeviceIds(getUsersWithDevice(deviceId), deviceId)

        removeDeviceStatus(userDeviceIds)
        updateUsers(deviceId)
        removeDevice(deviceQuery)
    }

    private fun getUsersWithDevice(deviceId: ObjectId): List<MongoUser> {
        return mongoTemplate.find(
            Query(Criteria.where("devices.device_id").`is`(deviceId)),
            MongoUser::class.java,
            MongoUser.COLLECTION_NAME
        )
    }

    private fun extractUserDeviceIds(usersWithDevice: List<MongoUser>, deviceId: ObjectId): List<String> =
        usersWithDevice.flatMap { user ->
            user.devices.filter { it?.deviceId == deviceId }
                .mapNotNull { it?.userDeviceId?.toHexString() }
        }

    private fun removeDeviceStatus(userDeviceIds: List<String>) =
        mongoTemplate.remove(
            Query(Criteria.where("user_device_id").`in`(userDeviceIds)),
            MongoDeviceStatus::class.java,
            MongoDeviceStatus.COLLECTION_NAME
        )

    private fun updateUsers(deviceId: ObjectId) =
        mongoTemplate.updateMulti(
            Query(),
            Update().pull("devices", Query(Criteria.where("device_id").`is`(deviceId))),
            MongoUser::class.java
        )

    private fun removeDevice(deviceQuery: Query) =
        mongoTemplate.remove(deviceQuery, MongoDevice::class.java, MongoDevice.COLLECTION_NAME)
}
