package com.sushkevych.securitydevices.device.infrastructure.dto.request

import jakarta.validation.constraints.NotEmpty

data class DeviceRequest(
    val id: String?,
    @field:NotEmpty(message = "Name cannot be empty.")
    val name: String,
    @field:NotEmpty(message = "Description cannot be empty.")
    val description: String,
    @field:NotEmpty(message = "Type cannot be empty.")
    val type: String,
    val attributes: List<DeviceAttributeRequest>
)

data class DeviceAttributeRequest(
    val attributeType: String?,
    val attributeValue: String?
)
