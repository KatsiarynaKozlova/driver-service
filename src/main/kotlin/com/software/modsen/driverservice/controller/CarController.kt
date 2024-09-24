package com.software.modsen.driverservice.controller

import com.software.modsen.driverservice.dto.request.CarRequest
import com.software.modsen.driverservice.dto.response.CarListResponse
import com.software.modsen.driverservice.dto.response.CarResponse
import com.software.modsen.driverservice.service.CarService
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
@RequestMapping("/cars")
class CarController(
    private val carService: CarService
) {
    @GetMapping("/{id}")
    fun getCarById(@PathVariable id: Long): ResponseEntity<CarResponse> =
        ResponseEntity.ok().body(carService.getCarById(id))

    @GetMapping
    fun getAllCars(): ResponseEntity<CarListResponse> = ResponseEntity.ok().body(carService.getAllCars())

    @PostMapping
    fun createCar(@RequestBody carRequest: CarRequest): ResponseEntity<CarResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(carRequest))

    @PutMapping("/{id}")
    fun updateCar(@PathVariable id: Long, @RequestBody carRequest: CarRequest): ResponseEntity<CarResponse> =
        ResponseEntity.ok().body(carService.updateCar(id, carRequest))

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun deleteCar(@PathVariable id: Long) = carService.deleteCar(id)
}
