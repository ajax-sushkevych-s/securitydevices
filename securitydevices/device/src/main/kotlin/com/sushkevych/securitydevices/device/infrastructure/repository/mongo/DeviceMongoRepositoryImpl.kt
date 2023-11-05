package com.sushkevych.securitydevices.device.infrastructure.repository.mongo

import com.sushkevych.securitydevices.device.application.port.DeviceRepository
import com.sushkevych.securitydevices.device.domain.Device
import com.sushkevych.securitydevices.device.infrastructure.mapper.toDevice
import com.sushkevych.securitydevices.device.infrastructure.mapper.toMongoDevice
import com.sushkevych.securitydevices.device.infrastructure.repository.entity.MongoDevice
import com.sushkevych.securitydevices.devicestatus.infrastructure.repository.entity.MongoDeviceStatus
import com.sushkevych.securitydevices.user.infrastructure.repository.entity.MongoUser
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class DeviceMongoRepositoryImpl(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) : DeviceRepository {

    override fun getById(id: String): Mono<Device> =
        reactiveMongoTemplate.findOne(
            Query(Criteria.where("id").`is`(id)),
            MongoDevice::class.java
        ).map { it.toDevice() }

    override fun findAll(): Flux<Device> =
        reactiveMongoTemplate.findAll(MongoDevice::class.java)
            .map { it.toDevice() }

    override fun save(entity: Device): Mono<Device> =
        reactiveMongoTemplate.save(entity.toMongoDevice()).map { it.toDevice() }

    override fun update(entity: Device): Mono<Device> {
        val query = Query(Criteria.where("id").`is`(entity.id))
        val update = Update().apply {
            set("name", entity.name)
            set("description", entity.description)
            set("type", entity.type)
            set("attributes", entity.attributes)
        }
        return reactiveMongoTemplate.updateFirst(query, update, MongoDevice::class.java)
            .handle { result, sink ->
                if (result.modifiedCount > 0) {
                    sink.next(entity)
                }
            }
    }

    override fun deleteById(id: String): Mono<Unit> =
        getUsersWithDevice(id)
            .collectList()
            .flatMap { usersWithDevice ->
                val deviceQuery = Query(Criteria.where("id").`is`(id))

                val userDeviceIds = extractUserDeviceIds(id, usersWithDevice)

                Flux.concat(
                    removeDeviceStatus(userDeviceIds),
                    updateUsers(id),
                    removeDevice(deviceQuery)
                ).then()
            }.thenReturn(Unit)

    private fun getUsersWithDevice(deviceId: String): Flux<MongoUser> =
        reactiveMongoTemplate.find(
            Query(Criteria.where("devices.deviceId").`is`(deviceId)),
            MongoUser::class.java
        )

    private fun extractUserDeviceIds(deviceId: String, usersWithDevice: List<MongoUser>): List<String> =
        usersWithDevice
            .asSequence()
            .flatMap { user -> user.devices.asSequence() }
            .filter { device -> device?.deviceId.toString() == deviceId }
            .mapNotNull { device -> device?.userDeviceId?.toHexString() }
            .toList()

    private fun removeDeviceStatus(userDeviceIds: List<String>): Mono<Unit> =
        Mono.fromSupplier { userDeviceIds }
            .flatMap { userDeviceIdList ->
                val query = Query(Criteria.where("userDeviceId").`in`(userDeviceIdList))
                reactiveMongoTemplate.remove(query, MongoDeviceStatus::class.java)
            }
            .thenReturn(Unit)

    private fun updateUsers(deviceId: String): Mono<Unit> =
        reactiveMongoTemplate.updateMulti(
            Query(),
            Update().pull("devices", Query(Criteria.where("deviceId").`is`(deviceId))),
            MongoUser::class.java
        ).thenReturn(Unit)

    private fun removeDevice(deviceQuery: Query): Mono<Unit> =
        reactiveMongoTemplate.remove(deviceQuery, MongoDevice::class.java)
            .thenReturn(Unit)
}
