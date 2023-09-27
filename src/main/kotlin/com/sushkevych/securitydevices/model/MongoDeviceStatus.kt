package com.sushkevych.securitydevices.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.sushkevych.securitydevices.model.MongoDeviceStatus.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("DeviceStatus")
@Document(value = COLLECTION_NAME)
data class MongoDeviceStatus(
    @Id
    @field:JsonProperty(value = "_id")
    val id: ObjectId? = null,
    val userDeviceId: String?,
    val status: MongoDeviceStatusType,
    val batteryLevel: Double?,
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
