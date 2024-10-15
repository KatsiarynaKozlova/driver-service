package com.software.modsen.driverservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.software.modsen.driverservice.config.DatabaseContainerConfiguration
import com.software.modsen.driverservice.dto.request.CarRequest
import com.software.modsen.driverservice.dto.response.CarResponse
import com.software.modsen.driverservice.util.ExceptionMessages
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.post
import org.springframework.web.servlet.function.RequestPredicates


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Import(DatabaseContainerConfiguration::class)
class CarControllerIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should return no content`() {
        mockMvc.delete("/cars/{id}", DEFAULT_ID)
            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @Order(1)
    fun `should return not found`() {
        mockMvc.get("/cars/{id}", DEFAULT_ID)
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.message") { value(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION.format(DEFAULT_ID)) }
            }
    }

    @Test
    @Order(2)
    fun `should return new car`() {
        val carRequest = defaultCarRequest
        val expectedCarResponse = defaultCarResponse
        mockMvc.post("/cars")
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest)
        }
            .andDo { print() }
            .andExpect {
                status { isCreated() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.carId") { value(expectedCarResponse.carId) }
                jsonPath("$.model") { value(expectedCarResponse.model) }
                jsonPath("$.licensePlate") { value(expectedCarResponse.licensePlate) }
                jsonPath("$.color") { value(expectedCarResponse.color) }
                jsonPath("$.year") { value(expectedCarResponse.year) }
            }
    }

    @Test
    @Order(3)
    fun `should return car by id`() {
        val expectedCarResponse = defaultCarResponse
        mockMvc.get("/cars/{id}", DEFAULT_ID)
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.carId") { value(expectedCarResponse.carId) }
                jsonPath("$.model") { value(expectedCarResponse.model) }
                jsonPath("$.licensePlate") { value(expectedCarResponse.licensePlate) }
                jsonPath("$.color") { value(expectedCarResponse.color) }
                jsonPath("$.year") { value(expectedCarResponse.year) }
            }
    }

    @Test
    @Order(4)
    fun `should update car by id and return updated`() {
        val updatedCar = defaultUpdatedCarRequest
        val defaultCar = defaultCarRequest
        mockMvc.put("/cars/{id}", DEFAULT_ID)
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updatedCar)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.carId") { value(DEFAULT_ID) }
                jsonPath("$.model") { value(updatedCar.model) }
                jsonPath("$.licensePlate") { value(updatedCar.licensePlate) }
                jsonPath("$.color") { value(updatedCar.color) }
                jsonPath("$.year") { value(updatedCar.year) }
            }
        mockMvc.put("/cars/{id}", DEFAULT_ID)
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(defaultCar)
        }
    }

    @Test
    fun `should return list of cars`() {
        val expectedCarResponseList = listOf(defaultCarResponse)
        mockMvc.get("/cars")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.items[0].carId") { value(expectedCarResponseList[0].carId) }
                jsonPath("$.items[0].model") { value(expectedCarResponseList[0].model) }
                jsonPath("$.items[0].licensePlate") { value(expectedCarResponseList[0].licensePlate) }
                jsonPath("$.items[0].color") { value(expectedCarResponseList[0].color) }
                jsonPath("$.items[0].year") { value(expectedCarResponseList[0].year) }
            }
    }

    @Test
    fun `should return car already exist on create`() {
        val carRequest = defaultCarRequestWithAlreadyExistingLicenseInDB
        mockMvc.post("/cars")
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(carRequest)
        }
            .andDo { print() }
            .andExpect {
                status { isConflict() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                    }
                    jsonPath("$.message") { value(ExceptionMessages.CAR_ALREADY_EXIST.format(DEFAULT_LICENSE_PLATE)) }
                }
            }
    }

    companion object {
        private const val DEFAULT_ID = 1L
        private const val DEFAULT_LICENSE_PLATE = "1234AB"
        private const val DEFAULT_MODEL = "Tesla Model S"
        private const val DEFAULT_YEAR = 2022
        private const val DEFAULT_COLOR = "Black"

        private const val DEFAULT_UPDATE_LICENSE_PLATE = "5678BB"
        private const val DEFAULT_UPDATE_MODEL = "Tesla Model A"
        private const val DEFAULT_UPDATE_YEAR = 2023
        private const val DEFAULT_UPDATE_COLOR = "White"

        val defaultCarRequest = CarRequest(
            model = DEFAULT_MODEL,
            year = DEFAULT_YEAR,
            licensePlate = DEFAULT_LICENSE_PLATE,
            color = DEFAULT_COLOR
        )

        val defaultCarResponse = CarResponse(
            carId = DEFAULT_ID,
            model = DEFAULT_MODEL,
            year = DEFAULT_YEAR,
            licensePlate = DEFAULT_LICENSE_PLATE,
            color = DEFAULT_COLOR
        )

        val defaultUpdatedCarRequest = CarRequest(
            model = DEFAULT_UPDATE_MODEL,
            year = DEFAULT_UPDATE_YEAR,
            licensePlate = DEFAULT_UPDATE_LICENSE_PLATE,
            color = DEFAULT_UPDATE_COLOR
        )

        val defaultCarRequestWithAlreadyExistingLicenseInDB = CarRequest(
            model = DEFAULT_UPDATE_MODEL,
            year = DEFAULT_UPDATE_YEAR,
            licensePlate = DEFAULT_LICENSE_PLATE,
            color = DEFAULT_COLOR
        )
    }
}
