package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoDevice
import com.sushkevych.securitydevices.model.MongoDeviceStatus
import com.sushkevych.securitydevices.model.MongoUser
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class DeviceQueryRepository(private val reactiveMongoTemplate: ReactiveMongoTemplate) : DeviceRepository {

    override fun getDeviceById(deviceId: ObjectId): Mono<MongoDevice> =
        reactiveMongoTemplate.findOne(
            Query(Criteria.where("id").`is`(deviceId)),
            MongoDevice::class.java
        )

    override fun findAll(): Flux<MongoDevice> =
        reactiveMongoTemplate.findAll(MongoDevice::class.java)

    override fun save(device: MongoDevice): Mono<MongoDevice> =
        reactiveMongoTemplate.save(device)

    override fun update(device: MongoDevice): Mono<MongoDevice> {
        val query = Query(Criteria.where("id").`is`(device.id))
        val update = Update().apply {
            set("name", device.name)
            set("description", device.description)
            set("type", device.type)
            set("attributes", device.attributes)
        }
        return reactiveMongoTemplate.updateFirst(query, update, MongoDevice::class.java)
            .handle { result, sink ->
                if (result.modifiedCount > 0) {
                    sink.next(device)
                } else {
                    sink.complete()
                }
            }
    }

    override fun deleteById(deviceId: ObjectId): Mono<Unit> =
        getUsersWithDevice(deviceId)
            .collectList()
            .flatMap { usersWithDevice ->
                val deviceQuery = Query(Criteria.where("id").`is`(deviceId))

                val userDeviceIds = extractUserDeviceIds(deviceId, usersWithDevice)

                Flux.concat(
                    removeDeviceStatus(userDeviceIds),
                    updateUsers(deviceId),
                    removeDevice(deviceQuery)
                ).then()
            }.thenReturn(Unit)

    private fun getUsersWithDevice(deviceId: ObjectId): Flux<MongoUser> =
        reactiveMongoTemplate.find(
            Query(Criteria.where("devices.deviceId").`is`(deviceId)),
            MongoUser::class.java
        )

    private fun extractUserDeviceIds(deviceId: ObjectId, usersWithDevice: List<MongoUser>): List<String> =
        usersWithDevice
            .asSequence()
            .flatMap { user -> user.devices.asSequence() }
            .filter { device -> device?.deviceId == deviceId }
            .mapNotNull { device -> device?.userDeviceId?.toHexString() }
            .toList()

    private fun removeDeviceStatus(userDeviceIds: List<String>): Mono<Unit> =
        Mono.fromSupplier { userDeviceIds }
            .flatMap { userDeviceIdList ->
                val query = Query(Criteria.where("userDeviceId").`in`(userDeviceIdList))
                reactiveMongoTemplate.remove(query, MongoDeviceStatus::class.java)
            }
            .thenReturn(Unit)

    private fun updateUsers(deviceId: ObjectId): Mono<Unit> =
        reactiveMongoTemplate.updateMulti(
            Query(),
            Update().pull("devices", Query(Criteria.where("deviceId").`is`(deviceId))),
            MongoUser::class.java
        ).thenReturn(Unit)

    private fun removeDevice(deviceQuery: Query): Mono<Unit> =
        reactiveMongoTemplate.remove(deviceQuery, MongoDevice::class.java)
            .thenReturn(Unit)
}
