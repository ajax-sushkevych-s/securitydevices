package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoDeviceStatus
import com.sushkevych.securitydevices.model.MongoUser
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepository(private val mongoTemplate: MongoTemplate) : UserRepository {
    override fun getUserById(id: ObjectId): MongoUser? {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, MongoUser::class.java, MongoUser.COLLECTION_NAME)
    }

    override fun findAll(): List<MongoUser> = mongoTemplate.findAll(MongoUser::class.java, MongoUser.COLLECTION_NAME)

    override fun save(user: MongoUser): MongoUser = mongoTemplate.save(user, MongoUser.COLLECTION_NAME)

    override fun deleteById(userId: ObjectId) {
        val matchUser = MatchOperation(
            Criteria.where("id").`is`(userId)
        )

        val matchDevices = MatchOperation(
            Criteria.where("devices").elemMatch(
                Criteria.where("role").`is`(MongoUser.MongoUserRole.OWNER)
            )
        )

        val aggregation = Aggregation.newAggregation(MongoUser::class.java, matchUser, matchDevices)

        mongoTemplate.aggregate(aggregation, MongoUser.COLLECTION_NAME, MongoUser::class.java)
            .mappedResults
            .forEach { user ->
                user.devices.forEach { device ->
                    mongoTemplate.remove(
                        Query(Criteria.where("user_device_id").`is`(device?.userDeviceId?.toHexString())),
                        MongoDeviceStatus::class.java,
                        MongoDeviceStatus.COLLECTION_NAME
                    )
                }
                mongoTemplate.remove(
                    Query(Criteria.where("id").`is`(user.id)),
                    MongoUser::class.java,
                    MongoUser.COLLECTION_NAME
                )
            }
    }

    override fun getUserByUserName(username: String): MongoUser? {
        val query = Query(Criteria.where("username").`is`(username))
        return mongoTemplate.findOne(query, MongoUser::class.java, MongoUser.COLLECTION_NAME)
    }

    override fun findUsersWithSpecificDevice(deviceId: ObjectId): List<MongoUser> {
        val query = Query(Criteria.where("devices.deviceId").`is`(deviceId))
        return mongoTemplate.find(query, MongoUser::class.java, MongoUser.COLLECTION_NAME)
    }

    override fun findUsersWithSpecificRole(role: MongoUser.MongoUserRole): List<MongoUser> {
        val query = Query(Criteria.where("devices.role").`is`(role))
        return mongoTemplate.find(query, MongoUser::class.java, MongoUser.COLLECTION_NAME)
    }

    override fun findUsersWithoutDevices(): List<MongoUser> {
        val query = Query(Criteria.where("devices").`is`(emptyList<Any>()))
        return mongoTemplate.find(query, MongoUser::class.java, MongoUser.COLLECTION_NAME)
    }
}
