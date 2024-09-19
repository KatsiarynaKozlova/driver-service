package com.software.modsen.driverservice.dto.response

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@AllArgsConstructor
@NoArgsConstructor
@Data
data class CarResponse (
    val carId: Long?,
    val color: String,
    val model: String,
    val licensePlate: String,
    val year: Int,
)
