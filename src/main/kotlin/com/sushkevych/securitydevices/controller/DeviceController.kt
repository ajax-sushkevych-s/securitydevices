package com.sushkevych.securitydevices.controller

import com.sushkevych.securitydevices.model.Device
import com.sushkevych.securitydevices.service.DeviceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping

@RestController
@RequestMapping("/api/devices")
class DeviceController @Autowired constructor(private val deviceService: DeviceService) {

    @GetMapping("/{deviceId}")
    fun getDeviceById(@PathVariable deviceId: Long) : Device {
        return deviceService.getDeviceById(deviceId)
    }

    @GetMapping
    fun getAllDevices(): List<Device> {
        return deviceService.getAllDevices()
    }

    @PostMapping
    fun createDevice(@RequestBody device: Device): Device {
        return deviceService.saveDevice(device)
    }

    @DeleteMapping("/{deviceId}")
    fun deleteDevice(@PathVariable deviceId: Long) {
        deviceService.deleteDevice(deviceId)
    }
}
