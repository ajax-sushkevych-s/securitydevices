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

        val usersWithDevice = mongoTemplate.find(
            Query(Criteria.where("devices.device_id").`is`(deviceId)),
            MongoUser::class.java,
            MongoUser.COLLECTION_NAME
        )
        val userDeviceIds = usersWithDevice.flatMap { user ->
            user.devices.filter { it?.deviceId == deviceId }
                .mapNotNull { it?.userDeviceId?.toHexString() }
        }

        val deviceStatusQuery = Query(Criteria.where("user_device_id").`in`(userDeviceIds))
        mongoTemplate.remove(deviceStatusQuery, MongoDeviceStatus::class.java, MongoDeviceStatus.COLLECTION_NAME)

        val update = Update().pull("devices", Query(Criteria.where("device_id").`is`(deviceId)))
        mongoTemplate.updateMulti(Query(), update, MongoUser::class.java)

        mongoTemplate.remove(deviceQuery, MongoDevice::class.java, MongoDevice.COLLECTION_NAME)
    }
}
