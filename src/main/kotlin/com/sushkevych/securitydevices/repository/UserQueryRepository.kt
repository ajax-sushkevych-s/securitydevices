package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoUser
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepository(private val mongoTemplate: MongoTemplate) : UserRepository {
    override fun getUserById(id: ObjectId): MongoUser? {
        val query = Query().addCriteria(Criteria.where("id").`is`(id))
        return mongoTemplate.findOne(query, MongoUser::class.java, MongoUser.COLLECTION_NAME)
    }

    override fun findAll(): List<MongoUser> {
        return mongoTemplate.findAll(MongoUser::class.java, MongoUser.COLLECTION_NAME)
    }

    override fun save(user: MongoUser): MongoUser {
        return mongoTemplate.save(user, MongoUser.COLLECTION_NAME)
    }

    override fun deleteById(id: ObjectId) {
        val query = Query(Criteria.where("id").`is`(id))
        mongoTemplate.remove(query, MongoUser::class.java, MongoUser.COLLECTION_NAME)
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
