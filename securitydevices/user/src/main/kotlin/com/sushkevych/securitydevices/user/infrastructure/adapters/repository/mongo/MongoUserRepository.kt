package com.sushkevych.securitydevices.user.infrastructure.adapters.repository.mongo

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.client.result.DeleteResult
import com.sushkevych.securitydevices.devicestatus.infrastructure.repository.entity.MongoDeviceStatus
import com.sushkevych.securitydevices.user.application.port.UserRepositoryOutPort
import com.sushkevych.securitydevices.user.domain.User
import com.sushkevych.securitydevices.user.infrastructure.mapper.toMongoUser
import com.sushkevych.securitydevices.user.infrastructure.mapper.toUser
import com.sushkevych.securitydevices.user.infrastructure.adapters.repository.entity.MongoUser
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Repository
@Suppress("TooManyFunctions")
class MongoUserRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    private val objectMapper: ObjectMapper
) : UserRepositoryOutPort {

    override fun getById(id: String): Mono<User> =
        reactiveMongoTemplate.findOne(
            Query().addCriteria(Criteria.where("id").`is`(ObjectId(id))),
            MongoUser::class.java
        )
            .map { it.toUser() }

    override fun findAll(): Flux<User> =
        reactiveMongoTemplate.findAll(MongoUser::class.java)
            .map { it.toUser() }

    override fun save(entity: User): Mono<User> = reactiveMongoTemplate.save(entity.toMongoUser()).map { it.toUser() }

    override fun update(entity: User): Mono<User> {
        return Mono.fromSupplier { entity.toMongoUser() }
            .flatMap { mongoUser ->
                val query = Query(Criteria.where("id").`is`(mongoUser.id))
                val update = Update().apply {
                    set("username", mongoUser.username)
                    set("email", mongoUser.email)
                    set("mobileNumber", mongoUser.mobileNumber)
                    set("password", mongoUser.password)
                    set("devices", mongoUser.devices)
                }

                reactiveMongoTemplate.updateFirst(query, update, MongoUser::class.java)
                    .flatMap { updateResult ->
                        if (updateResult.modifiedCount > 0) mongoUser.toMono() else Mono.empty()
                    }
            }
            .map { it.toUser() }
    }

    override fun deleteById(id: String): Mono<Unit> {
        return reactiveMongoTemplate.findAndRemove(
            Query(Criteria.where("id").`is`(ObjectId(id))),
            MongoUser::class.java
        ).flatMap { deletedUser ->
            deleteDeviceStatusForOwnerDevices(deletedUser)
        }.thenReturn(Unit)
    }

    private fun deleteDeviceStatusForOwnerDevices(userToDelete: MongoUser): Mono<DeleteResult> {
        val devicesToRemove: List<String?> = userToDelete.devices.asSequence()
            .filter { it?.role == MongoUser.MongoUserRole.OWNER }
            .map { it?.userDeviceId?.toHexString() }
            .toList()
        val query = Query(Criteria.where("userDeviceId").`in`(devicesToRemove))

        return reactiveMongoTemplate.remove(query, MongoDeviceStatus::class.java)
    }

    override fun getUserByUserName(username: String): Mono<User> {
        val query = Query(Criteria.where("username").`is`(username))
        return reactiveMongoTemplate.findOne(query, MongoUser::class.java)
            .map { it.toUser() }
    }

    override fun findUsersWithSpecificDevice(deviceId: ObjectId): Flux<User> {
        val query = Query(Criteria.where("devices.deviceId").`is`(deviceId))
        return reactiveMongoTemplate.find(query, MongoUser::class.java)
            .map { it.toUser() }
    }

    override fun findUsersWithSpecificRole(role: User.UserRole): Flux<User> {
        val query = Query(Criteria.where("devices.role").`is`(role))
        return reactiveMongoTemplate.find(query, MongoUser::class.java)
            .map { it.toUser() }
    }

    override fun findUsersWithoutDevices(): Flux<User> {
        val query = Query(Criteria.where("devices").`is`(emptyList<Any>()))
        return reactiveMongoTemplate.find(query, MongoUser::class.java)
            .map { it.toUser() }
    }

    override fun getUsersByOffsetPagination(offset: Int, limit: Int): Mono<Pair<List<User>, Long>> {
        val aggregation = TypedAggregation.newAggregation(
            MongoUser::class.java,
            Aggregation.skip(offset.toLong()),
            Aggregation.limit(limit.toLong()),
            Aggregation.facet()
                .and(Aggregation.project(MongoUser::class.java)).`as`("users")
                .and(Aggregation.count().`as`("totalCount")).`as`("totalCount")
        )

        return getUsersAndTotalCountFromAggregationResult(aggregation)
            .map { resultPair ->
                val users = resultPair.first.map { it.toUser() }
                val totalCount = resultPair.second
                Pair(users, totalCount)
            }
    }

    override fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Mono<Pair<List<User>, Long>> {
        var matchByCursor: AggregationOperation = Aggregation.match(Criteria())
        cursor?.let {
            matchByCursor = Aggregation.match(Criteria.where("_id").gt(ObjectId(cursor)))
        }
        val aggregation = TypedAggregation.newAggregation(
            MongoUser::class.java,
            matchByCursor,
            Aggregation.sort(Sort.by(Sort.Order.asc("_id"))),
            Aggregation.limit(pageSize.toLong()),
            Aggregation.facet()
                .and(Aggregation.project(MongoUser::class.java)).`as`("users")
                .and(Aggregation.count().`as`("totalCount")).`as`("totalCount")
        )

        return getUsersAndTotalCountFromAggregationResult(aggregation)
            .map { resultPair ->
                val users = resultPair.first.map { it.toUser() }
                val totalCount = resultPair.second
                Pair(users, totalCount)
            }
    }

    private fun getUsersAndTotalCountFromAggregationResult(
        aggregation: TypedAggregation<MongoUser>
    ): Mono<Pair<List<MongoUser>, Long>> {
        return reactiveMongoTemplate.aggregate(aggregation, Map::class.java)
            .collectList()
            .flatMap { result ->
                Pair(
                    extractUsers(result),
                    extractTotalCount(result).toLong()
                ).toMono()
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractTotalCount(result: MutableList<Map<*, *>>): Int {
        val totalCount = result.getOrNull(0)?.get("totalCount") as? List<LinkedHashMap<*, *>>
        return totalCount?.getOrNull(0)?.get("totalCount") as? Int ?: 0
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractUsers(result: MutableList<Map<*, *>>): List<MongoUser> {
        val pagedUsers = result.getOrNull(0)?.get("users") as? List<LinkedHashMap<*, *>>
        val users = pagedUsers?.takeIf { it.isNotEmpty() }?.map { userMap ->
            val modifiedUserMap = modifyObjectIdToHexId(userMap)
            objectMapper.convertValue(modifiedUserMap, MongoUser::class.java)
        } ?: emptyList()
        return users
    }

    @Suppress("UNCHECKED_CAST")
    private fun modifyObjectIdToHexId(userMap: LinkedHashMap<*, *>): MutableMap<Any, Any> {
        val idValue = userMap["_id"] as ObjectId
        val devicesList = userMap["devices"] as List<Map<String, Any>>

        val modifiedDevicesList = devicesList.map { deviceMap ->
            val deviceIdValue = deviceMap["deviceId"] as ObjectId
            val userDeviceIdValue = deviceMap["userDeviceId"] as ObjectId
            deviceMap.toMutableMap().apply {
                this["deviceId"] = deviceIdValue.toHexString()
                this["userDeviceId"] = userDeviceIdValue.toHexString()
            }
        }

        return userMap.toMutableMap().apply {
            this.remove("_id")
            this["id"] = idValue.toHexString()
            this["devices"] = modifiedDevicesList
        }
    }
}
