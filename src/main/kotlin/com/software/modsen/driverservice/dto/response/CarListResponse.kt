package com.software.modsen.driverservice.dto.response

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@AllArgsConstructor
@NoArgsConstructor
@Data
data class CarListResponse(
    val carListResponse: List<CarResponse>
)
