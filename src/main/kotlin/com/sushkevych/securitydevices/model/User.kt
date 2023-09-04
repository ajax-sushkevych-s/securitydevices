package com.sushkevych.securitydevices.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
@Table(name = "users")
class User(
    @Column(name = "username")
    var username: String = "",
    @Column(name = "email")
    var email: String = "",
    @Column(name = "mobile_number")
    var mobileNumber:String = "",
    @Column(name = "password")
    var password: String = "",
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
)
