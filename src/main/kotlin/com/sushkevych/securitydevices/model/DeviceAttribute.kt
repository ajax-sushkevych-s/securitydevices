package com.sushkevych.securitydevices.model

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
@Table(name = "device_attribute")
class DeviceAttribute(
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null,
    @ManyToOne
    @JoinColumn(name = "device_id")
    var device: Device? = null,
    @ManyToOne
    @JoinColumn(name = "attribute_id")
    var attribute: DeviceAttributeValue? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
)
