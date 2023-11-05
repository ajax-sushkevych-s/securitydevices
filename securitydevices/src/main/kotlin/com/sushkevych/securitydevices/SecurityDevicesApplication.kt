package com.sushkevych.securitydevices

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SecurityDevicesApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<SecurityDevicesApplication>(*args)
}
