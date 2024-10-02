package com.software.modsen.driverservice.controller

import com.software.modsen.driverservice.dto.request.DriverRequest
import com.software.modsen.driverservice.dto.response.DriverListResponse
import com.software.modsen.driverservice.dto.response.DriverResponse
import com.software.modsen.driverservice.dto.response.DriverWithCarResponse
import com.software.modsen.driverservice.mapper.toDriverResponse
import com.software.modsen.driverservice.mapper.toDriverWithCarResponse
import com.software.modsen.driverservice.service.DriverService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
    @Operation(
        description = "Get Driver by ID ",
        parameters = [Parameter(name = "id", description = "This is the Driver ID that will be searched for")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Found Driver by ID",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = DriverWithCarResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404", description = "Driver not found",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun gerDriverById(@PathVariable id: Long): ResponseEntity<DriverWithCarResponse> =
        ResponseEntity.ok(driverService.getDriverById(id).toDriverWithCarResponse())

    @GetMapping
    @Operation(description = "Get list of all Drivers")
    @ApiResponse(
        responseCode = "200", description = "List of all Drivers",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = DriverListResponse::class)
            )
        ]
    )
    fun getAllDrivers(): ResponseEntity<DriverListResponse> =
        ResponseEntity.ok(DriverListResponse(driverService.getAllDrivers().map { it.toDriverResponse() }))

    @PostMapping
    @Operation(
        description = "Create Driver",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = DriverRequest::class)
            )]
        )
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Create Driver",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = DriverResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "409", description = "Driver already exist",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun createDriver(@RequestBody driverRequest: DriverRequest): ResponseEntity<DriverResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(driverRequest).toDriverResponse())

    @PutMapping("/{id}")
    @Operation(
        description = "Update Driver",
        parameters = [Parameter(name = "id", description = "This is the Driver ID that will be updated")],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = DriverRequest::class)
            )]
        )
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Update Driver",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = DriverResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "404", description = "Driver not found",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "409", description = "Driver already exist",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun updateDriver(
        @PathVariable id: Long,
        @RequestBody driverRequest: DriverRequest
    ): ResponseEntity<DriverResponse> =
        ResponseEntity.ok(driverService.updateDriver(id, driverRequest).toDriverResponse())

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(
        description = "Delete Driver by ID",
        parameters = [Parameter(name = "id", description = "This is the driver ID that will be deleted")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204", description = "Delete Driver"
            ),
            ApiResponse(
                responseCode = "404", description = "Driver not found",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    fun deleteDriver(@PathVariable id: Long) = driverService.deleteDriver(id)
}
