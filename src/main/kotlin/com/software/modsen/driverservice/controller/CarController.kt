package com.software.modsen.driverservice.controller

import com.software.modsen.driverservice.dto.request.CarRequest
import com.software.modsen.driverservice.dto.response.CarListResponse
import com.software.modsen.driverservice.dto.response.CarResponse
import com.software.modsen.driverservice.mapper.toCarResponse
import com.software.modsen.driverservice.service.CarService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "Cars")
class CarController(
    private val carService: CarService
) {
    @GetMapping("/{id}")
    @Operation(
        description = "Get Car by ID ",
        parameters = [Parameter(name = "id", description = "This is the car ID that will be searched for")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Found Car by ID",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = CarResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404", description = "Car not found",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun getCarById(@PathVariable id: Long): ResponseEntity<CarResponse> =
        ResponseEntity.ok(carService.getCarById(id).toCarResponse())

    @GetMapping
    @Operation(description = "Get list of all Cars")
    @ApiResponse(
        responseCode = "200", description = "List of all Cars",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = CarListResponse::class)
            )
        ]
    )
    fun getAllCars(): ResponseEntity<CarListResponse> =
        ResponseEntity.ok(CarListResponse(carService.getAllCars().map { it.toCarResponse() }))

    @PostMapping
    @Operation(
        description = "Create Car",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = CarRequest::class)
            )]
        )
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Create Car",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CarResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "409", description = "Car already exist",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun createCar(@RequestBody carRequest: CarRequest): ResponseEntity<CarResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(carRequest).toCarResponse())

    @PutMapping("/{id}")
    @Operation(
        description = "Update Car",
        parameters = [Parameter(name = "id", description = "This is the car ID that will be updated")],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = CarRequest::class)
            )]
        )
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Update Car",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CarResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Car not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "409", description = "Car already exist",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun updateCar(@PathVariable id: Long, @RequestBody carRequest: CarRequest): ResponseEntity<CarResponse> =
        ResponseEntity.ok(carService.updateCar(id, carRequest).toCarResponse())

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
        description = "Delete Car by ID",
        parameters = [Parameter(name = "id", description = "This is the car ID that will be deleted")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204", description = "Delete Car"
            ),
            ApiResponse(
                responseCode = "404", description = "Car not found",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun deleteCar(@PathVariable id: Long) = carService.deleteCar(id)
}
