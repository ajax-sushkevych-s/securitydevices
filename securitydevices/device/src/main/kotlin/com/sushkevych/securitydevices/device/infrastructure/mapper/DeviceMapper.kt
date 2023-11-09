package com.sushkevych.securitydevices.device.infrastructure.mapper

import com.sushkevych.securitydevices.device.domain.Device
import com.sushkevych.securitydevices.commonmodels.device.Device as ProtoDevice
import com.sushkevych.securitydevices.commonmodels.device.DeviceAttribute as ProtoDeviceAttribute
import com.sushkevych.securitydevices.device.infrastructure.dto.response.DeviceAttributeResponse
import com.sushkevych.securitydevices.device.infrastructure.dto.response.DeviceResponse
import com.sushkevych.securitydevices.device.infrastructure.adapters.repository.entity.MongoDevice

fun Device.toMongoDevice(): MongoDevice {
    return MongoDevice(
        id = id,
        name = name,
        description = description,
        type = type,
        attributes = attributes.map { it?.toMongoDeviceAttribute() }
    )
}

fun Device.DeviceAttribute?.toMongoDeviceAttribute(): MongoDevice.MongoDeviceAttribute? {
    return this?.let {
        MongoDevice.MongoDeviceAttribute(
            attributeType = it.attributeType,
            attributeValue = it.attributeValue
        )
    }
}

fun Device.toProtoDevice(): ProtoDevice {
    return ProtoDevice.newBuilder()
        .setId(this.id)
        .setName(this.name)
        .setDescription(this.description)
        .setType(this.type)
        .addAllAttributes(this.attributes.map { it?.toProtoDeviceAttribute() })
        .build()
}

fun Device.DeviceAttribute.toProtoDeviceAttribute(): ProtoDeviceAttribute {
    return ProtoDeviceAttribute.newBuilder()
        .setAttributeType(this.attributeType)
        .setAttributeValue(this.attributeValue)
        .build()
}

fun Device.toDeviceResponse(): DeviceResponse {
    return DeviceResponse(
        id = id,
        name = name,
        description = description,
        type = type,
        attributes = attributes.map { it?.toDeviceAttributeResponse() }
    )
}

fun Device.DeviceAttribute?.toDeviceAttributeResponse(): DeviceAttributeResponse? {
    return this?.let {
        DeviceAttributeResponse(
            attributeType = it.attributeType,
            attributeValue = it.attributeValue
        )
    }
}
