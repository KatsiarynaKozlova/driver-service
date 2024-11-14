package com.software.modsen.driverservice.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class InitDriverRequest(
    @field:NotBlank(message = "Name cannot be blank")
    val name: String,
    @field:Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "Email is not valid"
    )
    val email: String,
    @field:Pattern(
        regexp = "^\\d{9,15}$",
        message = "Phone is not valid"
    )
    val phone: String
)
