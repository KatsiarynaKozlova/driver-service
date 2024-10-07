package com.software.modsen.driverservice.dto.response

import com.software.modsen.driverservice.model.DriverSex

data class DriverWithCarResponse(
    val driverId: Long?,
    val name: String,
    val email: String,
    val phone: String,
    val sex: DriverSex,
    val car: CarResponse
)
