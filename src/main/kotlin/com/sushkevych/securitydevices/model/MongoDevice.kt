package com.sushkevych.securitydevices.model

import com.sushkevych.securitydevices.model.MongoDevice.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@TypeAlias("Device")
@Document(value = COLLECTION_NAME)
data class MongoDevice(
    @Id
    val id: ObjectId?,
    val name: String?,
    val description: String?,
    val type: String?,
    val attributes: List<MongoDeviceAttribute?>
) {
    @TypeAlias("DeviceAttribute")
    @Document
    data class MongoDeviceAttribute(
        @Field(value = "attribute_value")
        val attributeValue: String?,
        @Field(value = "attribute_type")
        val attributeType: String?
    )

    companion object {
        const val COLLECTION_NAME = "device"
    }
}
