package com.sushkevych.securitydevices.device.infrastructure.mapper

import com.sushkevych.securitydevices.commonmodels.device.Device as ProtoDevice
import com.sushkevych.securitydevices.commonmodels.device.DeviceAttribute as ProtoDeviceAttribute
import com.sushkevych.securitydevices.device.domain.Device

fun ProtoDevice.toDevice(): Device {
    return Device(
        id = this.id,
        name = this.name,
        description = this.description,
        type = this.type,
        attributes = this.attributesList.map { it.toDeviceAttribute() }
    )
}

fun ProtoDeviceAttribute.toDeviceAttribute(): Device.DeviceAttribute {
    return Device.DeviceAttribute(
        attributeType = this.attributeType,
        attributeValue = this.attributeValue
    )
}
