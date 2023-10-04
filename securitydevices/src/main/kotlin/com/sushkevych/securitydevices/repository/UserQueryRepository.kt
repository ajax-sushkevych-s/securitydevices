package com.sushkevych.securitydevices.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.sushkevych.securitydevices.model.MongoDeviceStatus
import com.sushkevych.securitydevices.model.MongoUser
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
@Suppress("TooManyFunctions")
class UserQueryRepository(private val mongoTemplate: MongoTemplate, private val objectMapper: ObjectMapper) :
    UserRepository {
    override fun getUserById(id: ObjectId): MongoUser? {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, MongoUser::class.java)
    }

    override fun findAll(): List<MongoUser> = mongoTemplate.findAll(MongoUser::class.java)

    override fun save(user: MongoUser): MongoUser = mongoTemplate.save(user)

    override fun deleteById(userId: ObjectId) {
        mongoTemplate.findAndRemove(
            Query(Criteria.where("id").`is`(userId)), MongoUser::class.java
        )?.let { deleteDeviceStatusForOwnerDevices(it) }
    }

    private fun deleteDeviceStatusForOwnerDevices(userToDelete: MongoUser) {
        val devicesToRemove: List<String?> = userToDelete.devices.asSequence()
            .filter { it?.role == MongoUser.MongoUserRole.OWNER }
            .map { it?.userDeviceId?.toHexString() }
            .toList()
        mongoTemplate.remove(
            Query(Criteria.where("user_device_id").`in`(devicesToRemove)), MongoDeviceStatus::class.java
        )
    }

    override fun getUserByUserName(username: String): MongoUser? {
        val query = Query(Criteria.where("username").`is`(username))
        return mongoTemplate.findOne(query, MongoUser::class.java)
    }

    override fun findUsersWithSpecificDevice(deviceId: ObjectId): List<MongoUser> {
        val query = Query(Criteria.where("devices.deviceId").`is`(deviceId))
        return mongoTemplate.find(query, MongoUser::class.java)
    }

    override fun findUsersWithSpecificRole(role: MongoUser.MongoUserRole): List<MongoUser> {
        val query = Query(Criteria.where("devices.role").`is`(role))
        return mongoTemplate.find(query, MongoUser::class.java)
    }

    override fun findUsersWithoutDevices(): List<MongoUser> {
        val query = Query(Criteria.where("devices").`is`(emptyList<Any>()))
        return mongoTemplate.find(query, MongoUser::class.java)
    }

    override fun getUsersByOffsetPagination(offset: Int, limit: Int): Pair<List<MongoUser>, Long> {
        val aggregation = TypedAggregation.newAggregation(
            MongoUser::class.java,
            Aggregation.skip(offset.toLong()),
            Aggregation.limit(limit.toLong()),
            Aggregation.facet()
                .and(Aggregation.project(MongoUser::class.java)).`as`("users")
                .and(Aggregation.count().`as`("totalCount")).`as`("totalCount")
        )

        return getUsersAndTotalCountFromAggregationResult(aggregation)
    }

    override fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Pair<List<MongoUser>, Long> {
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
    }

    @Suppress("UNCHECKED_CAST")
    private fun getUsersAndTotalCountFromAggregationResult(
        aggregation: TypedAggregation<MongoUser>
    ): Pair<List<MongoUser>, Long> {
        val results = mongoTemplate.aggregate(aggregation, Map::class.java)

        val pagedUsers = results.getMappedResults().first()["users"] as List<LinkedHashMap<*, *>>
        val users = pagedUsers.map { userMap ->
            val modifiedUserMap = modifyObjectIdToHexId(userMap)
            objectMapper.convertValue(modifiedUserMap, MongoUser::class.java)
        }

        val totalCountList = results.mappedResults.first()["totalCount"] as? List<LinkedHashMap<*, *>>
        val totalCount = ((totalCountList?.firstOrNull()?.get("totalCount") as? Int?)?.toLong()) ?: 0

        return Pair(users, totalCount)
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
