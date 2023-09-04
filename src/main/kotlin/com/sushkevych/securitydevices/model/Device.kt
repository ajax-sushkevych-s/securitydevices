package com.sushkevych.securitydevices.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
@Table(name = "devices")
class Device (
    @Column(name = "name")
    var name: String = "",
    @Column(name = "description")
    var description: String = "",
    @Column(name = "type")
    var type: String = "",
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
)
