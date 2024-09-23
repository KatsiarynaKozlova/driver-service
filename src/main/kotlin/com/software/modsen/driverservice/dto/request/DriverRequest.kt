package com.software.modsen.driverservice.dto.request

data class DriverRequest(
    val name: String,
    val email: String,
    val phone: String,
    val sex: String,
    val carId: Long
)
