package com.sushkevych.securitydevices.device.infrastructure.dto.response

data class DeviceResponse(
    val id: String?,
    val name: String?,
    val description: String?,
    val type: String?,
    val attributes: List<DeviceAttributeResponse?>
)

data class DeviceAttributeResponse(
    val attributeType: String?,
    val attributeValue: String?
)
