package com.software.modsen.driverservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.software.modsen.driverservice.dto.request.CarRequest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.*
import org.springframework.web.servlet.function.RequestPredicates


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder( MethodOrderer.OrderAnnotation::class)
class CarControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should return no content`() {
        mockMvc.delete("/cars/"+DEFAULT_ID)
            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @Order(1)
    fun `should return list of cars`() {
        val car = getDefaultCarRequest()
        val expectedResult = listOf(car)
        mockMvc.post("/cars")
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(car)
        }
            .andDo { print() }
            .andExpect {
                status { isCreated() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.model") { value(car.model) }
                jsonPath("$.licensePlate") { value(car.licensePlate) }
                jsonPath("$.color") { value(car.color) }
                jsonPath("$.year") { value(car.year) }
            }
        mockMvc.get("/cars")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.items[0].model") { value(expectedResult[0].model) }
                jsonPath("$.items[0].licensePlate") { value(expectedResult[0].licensePlate) }
                jsonPath("$.items[0].color") { value(expectedResult[0].color) }
                jsonPath("$.items[0].year") { value(expectedResult[0].year) }
            }
    }

    @Test
    @Order(2)
    fun `should return car by id`() {
        val updatedCar = getDefaultUpdatedCar()
        val car = getDefaultCarRequest()
        mockMvc.put("/cars/1")
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
        mockMvc.get("/cars/"+DEFAULT_ID)
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.carId") { value(DEFAULT_ID) }
                jsonPath("$.model") { value(updatedCar.model) }
                jsonPath("$.licensePlate") { value(updatedCar.licensePlate) }
                jsonPath("$.color") { value(updatedCar.color) }
                jsonPath("$.year") { value(updatedCar.year) }
            }
    }

    private val DEFAULT_ID = 1L
    private fun getDefaultCarRequest() =
        CarRequest(
            model = "Tesla Model S",
            year = 2022,
            licensePlate = "1234AB",
            color = "Black"
        )

    private fun getDefaultUpdatedCar() =
        CarRequest(
            model = "Tesla Model A",
            year = 2023,
            licensePlate = "5678BB",
            color = "White"
        )
}
