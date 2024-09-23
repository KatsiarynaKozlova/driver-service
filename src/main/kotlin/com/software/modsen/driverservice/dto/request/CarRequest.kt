package com.software.modsen.driverservice.dto.request

import com.software.modsen.driverservice.model.Car

data class CarRequest(
    val color: String,
    val model: String,
    val licensePlate: String,
    val year: Int,
)
