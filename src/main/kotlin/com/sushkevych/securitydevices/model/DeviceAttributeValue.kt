package com.sushkevych.securitydevices.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
@Table(name = "device_attribute_value")
class DeviceAttributeValue(
    @Column(name = "attribute_name")
    var attributeName: String = "",
    @Column(name = "attribute_value")
    var attributeValue: String = "",
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
)
