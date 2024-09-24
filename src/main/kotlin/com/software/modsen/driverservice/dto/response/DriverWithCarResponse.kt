package com.software.modsen.driverservice.dto.response

data class DriverWithCarResponse(
    val driverId: Long?,
    val name: String,
    val email: String,
    val phone: String,
    val sex: String,
    val car: CarResponse
)
