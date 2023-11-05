package com.sushkevych.securitydevices.device.domain

data class Device(
    val id: String?,
    val name: String?,
    val description: String?,
    val type: String?,
    val attributes: List<DeviceAttribute?>
) {
    data class DeviceAttribute(
        val attributeType: String?,
        val attributeValue: String?
    )
}
