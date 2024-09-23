package com.software.modsen.driverservice.dto.request

import com.software.modsen.driverservice.model.Driver

data class DriverRequest(
    val name: String,
    val email: String,
    val phone: String,
    val sex: String,
    val carId: Long
)
