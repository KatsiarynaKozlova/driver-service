package com.software.modsen.driverservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.software.modsen.driverservice.config.DatabaseContainerConfiguration
import com.software.modsen.driverservice.config.KafkaContainerConfiguration
import com.software.modsen.driverservice.dto.request.CarRequest
import com.software.modsen.driverservice.dto.request.DriverRequest
import com.software.modsen.driverservice.dto.response.CarResponse
import com.software.modsen.driverservice.dto.response.DriverResponse
import com.software.modsen.driverservice.dto.response.DriverWithCarResponse
import com.software.modsen.driverservice.model.DriverSex
import com.software.modsen.driverservice.util.ExceptionMessages
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.servlet.function.RequestPredicates

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Import(DatabaseContainerConfiguration::class, KafkaContainerConfiguration::class)
class DriverControllerIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should return no content`() {
        mockMvc.delete("/drivers/{id}", DEFAULT_ID)
            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    @Order(1)
    fun `should return not found`() {
        mockMvc.get("/drivers/{id}", DEFAULT_ID)
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.message") {
                    value(
                        ExceptionMessages.DRIVER_NOT_FOUND_EXCEPTION.format(DEFAULT_ID)
                    )
                }
            }
    }

    @Test
    @Order(2)
    fun `should return new driver`() {
        setupCar()
        val driverRequest = getDefaultDriverRequest()
        val expectedDriverResponse = getDefaultDriverResponse()
        mockMvc.post("/drivers")
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(driverRequest)
        }
            .andDo { print() }
            .andExpect {
                status { isCreated() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.driverId") { value(expectedDriverResponse.driverId) }
                jsonPath("$.name") { value(expectedDriverResponse.name) }
                jsonPath("$.email") { value(expectedDriverResponse.email) }
                jsonPath("$.phone") { value(expectedDriverResponse.phone) }
            }
    }

    @Test
    @Order(3)
    fun `should return driver with car by id`() {
        val expectedDriverResponse = getDefaultDriverResponseWithCar()
        mockMvc.get("/drivers/{id}", DEFAULT_ID)
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.driverId") { value(expectedDriverResponse.driverId) }
                jsonPath("$.name") { value(expectedDriverResponse.name) }
                jsonPath("$.email") { value(expectedDriverResponse.email) }
                jsonPath("$.phone") { value(expectedDriverResponse.phone) }
                jsonPath("$.car.carId") { value(expectedDriverResponse.car.carId) }
                jsonPath("$.car.color") { value(expectedDriverResponse.car.color) }
                jsonPath("$.car.model") { value(expectedDriverResponse.car.model) }
                jsonPath("$.car.licensePlate") { value(expectedDriverResponse.car.licensePlate) }
                jsonPath("$.car.year") { value(expectedDriverResponse.car.year) }
            }
    }

    @Test
    @Order(4)
    fun `should update driver by id and return updated`() {
        val updatedDriver = getDefaultUpdatedDriverRequest()
        val expectedDriverResponse = getDefaultUpdatedDriver()
        val defaultDriver = getDefaultDriverRequest()
        mockMvc.put("/drivers/{id}", DEFAULT_ID)
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updatedDriver)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.driverId") { value(expectedDriverResponse.driverId) }
                jsonPath("$.name") { value(expectedDriverResponse.name) }
                jsonPath("$.email") { value(expectedDriverResponse.email) }
                jsonPath("$.phone") { value(expectedDriverResponse.phone) }
            }
        mockMvc.put("/drivers/{id}", DEFAULT_ID)
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(defaultDriver)
        }
    }

    @Test
    fun `should return list of drivers`() {
        val expectedDriverResponseList = listOf(getDefaultDriverResponse())
        mockMvc.get("/drivers")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.items[0].driverId") { value(expectedDriverResponseList[0].driverId) }
                jsonPath("$.items[0].name") { value(expectedDriverResponseList[0].name) }
                jsonPath("$.items[0].email") { value(expectedDriverResponseList[0].email) }
                jsonPath("$.items[0].phone") { value(expectedDriverResponseList[0].phone) }
            }
    }

    private fun setupCar() {
        mockMvc.post("/cars")
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(getDefaultCarRequest())
        }
    }

    companion object {
        private val DEFAULT_ID = 1L
        private val DEFAULT_CAR_ID = 1L
    }

    private fun getDefaultCarRequest() =
        CarRequest(
            model = "Tesla Model S",
            year = 2022,
            licensePlate = "1234AB",
            color = "Black"
        )

    private fun getDefaultCarResponse() =
        CarResponse(
            carId = 1L,
            model = "Tesla Model S",
            year = 2022,
            licensePlate = "1234AB",
            color = "Black"
        )

    private fun getDefaultUpdatedDriver() =
        DriverResponse(
            driverId = 1L,
            name = "Igor",
            email = "comedy@mail.ru",
            phone = "0987654321",
            sex = DriverSex.M
        )

    private fun getDefaultDriverRequest() =
        DriverRequest(
            name = "Igor",
            email = "uralskipelmen@mail.ru",
            phone = "1234567890",
            sex = "M",
            carId = DEFAULT_CAR_ID
        )

    private fun getDefaultUpdatedDriverRequest() =
        DriverRequest(
            name = "Igor",
            email = "comedy@mail.ru",
            phone = "0987654321",
            sex = "M",
            carId = DEFAULT_CAR_ID
        )

    private fun getDefaultDriverResponse() =
        DriverResponse(
            driverId = DEFAULT_ID,
            name = "Igor",
            email = "uralskipelmen@mail.ru",
            phone = "1234567890",
            sex = DriverSex.M
        )

    private fun getDefaultDriverResponseWithCar() =
        DriverWithCarResponse(
            driverId = DEFAULT_ID,
            name = "Igor",
            email = "uralskipelmen@mail.ru",
            phone = "1234567890",
            sex = DriverSex.M,
            car = getDefaultCarResponse()
        )
}
