package com.sushkevych.securitydevices.model

import com.sushkevych.securitydevices.model.MongoUser.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@TypeAlias("User")
@Document(value = COLLECTION_NAME)
data class MongoUser(
    @Id
    val id: ObjectId?,
    val username: String?,
    val email: String?,
    @Field(value = "mobile_number")
    val mobileNumber: String?,
    val password: String?,
    val devices: List<MongoUserDevice>?
) {
    @TypeAlias("UserDevice")
    @Document
    data class MongoUserDevice(
        @Field(value = "device_id")
        val deviceId: ObjectId?,
        @Field(value = "user_device_id")
        val userDeviceId: ObjectId?,
        val role: MongoUserRole?
    )

    enum class MongoUserRole {
        OWNER,
        VIEWER
    }

    companion object {
        const val COLLECTION_NAME = "user"
    }
}
