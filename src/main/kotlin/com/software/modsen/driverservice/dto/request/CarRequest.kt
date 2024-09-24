package com.software.modsen.driverservice.dto.request

data class CarRequest(
    val color: String,
    val model: String,
    val licensePlate: String,
    val year: Int,
)
