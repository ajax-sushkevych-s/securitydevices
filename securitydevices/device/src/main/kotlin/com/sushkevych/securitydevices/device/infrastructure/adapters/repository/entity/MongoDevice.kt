package com.sushkevych.securitydevices.device.infrastructure.adapters.repository.entity

import com.sushkevych.securitydevices.device.infrastructure.adapters.repository.entity.MongoDevice.Companion.COLLECTION_NAME
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Device")
@Document(value = COLLECTION_NAME)
data class MongoDevice(
    @Id
    val id: String?,
    val name: String?,
    val description: String?,
    val type: String?,
    val attributes: List<MongoDeviceAttribute?>
) {
    @TypeAlias("DeviceAttribute")
    @Document
    data class MongoDeviceAttribute(
        val attributeType: String?,
        val attributeValue: String?
    )

    companion object {
        const val COLLECTION_NAME = "device"
    }
}
