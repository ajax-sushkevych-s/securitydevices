package com.sushkevych.securitydevices.model

import com.sushkevych.securitydevices.model.MongoDeviceStatus.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@TypeAlias("DeviceStatus")
@Document(value = COLLECTION_NAME)
data class MongoDeviceStatus(
    @Id
    val id: ObjectId? = null,
    @Field(value = "user_device_id")
    val userDeviceId: String?,
    val status: MongoDeviceStatusType,
    @Field(value = "battery_level")
    val batteryLevel: Double?,
    @Field(value = "status_details")
    val statusDetails: Map<String, Any>?
) {
    enum class MongoDeviceStatusType {
        ONLINE,
        OFFLINE,
        AUTHORIZATION
    }

    companion object {
        const val COLLECTION_NAME = "device_status"
    }
}
