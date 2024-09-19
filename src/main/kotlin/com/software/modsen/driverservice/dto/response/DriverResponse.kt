package com.software.modsen.driverservice.dto.response

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@AllArgsConstructor
@NoArgsConstructor
@Data
data class DriverResponse(
    val driverId: Long?,
    val name: String,
    val email: String,
    val phone: String,
    val sex: String
)
