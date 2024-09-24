package com.software.modsen.driverservice.controller

import com.software.modsen.driverservice.dto.request.DriverRequest
import com.software.modsen.driverservice.dto.response.DriverListResponse
import com.software.modsen.driverservice.dto.response.DriverResponse
import com.software.modsen.driverservice.dto.response.DriverWithCarResponse
import com.software.modsen.driverservice.service.DriverService
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
@RequiredArgsConstructor
@RequestMapping("/drivers")
class DriverController(
    private val driverService: DriverService
) {
    @GetMapping("/{id}")
    fun gerDriverById(@PathVariable id: Long): ResponseEntity<DriverWithCarResponse> =
        ResponseEntity.ok().body(driverService.getDriverById(id))

    @GetMapping
    fun getAllDrivers(): ResponseEntity<DriverListResponse> = ResponseEntity.ok().body(driverService.getAllDrivers())

    @PostMapping
    fun createDriver(@RequestBody driverRequest: DriverRequest): ResponseEntity<DriverResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(driverRequest))

    @PutMapping("/{id}")
    fun updateDriver(
        @PathVariable id: Long,
        @RequestBody driverRequest: DriverRequest
    ): ResponseEntity<DriverResponse> = ResponseEntity.ok().body(driverService.updateDriver(id, driverRequest))

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun deleteDriver(@PathVariable id: Long) = driverService.deleteDriver(id)
}
