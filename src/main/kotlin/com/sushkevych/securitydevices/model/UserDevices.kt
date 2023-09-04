package com.sushkevych.securitydevices.model

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
@Table(name = "user_devices")
class UserDevices(
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null,
    @ManyToOne
    @JoinColumn(name = "device_id")
    var device: Device? = null,
    @Column(name = "role") // TODO Owner or Viewer
    var role: String = "",
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
)
