package com.software.modsen.driverservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.software.modsen.driverservice.config.DatabaseContainerConfiguration
import com.software.modsen.driverservice.config.KafkaContainerConfiguration
import com.software.modsen.driverservice.dto.request.CarRequest
import com.software.modsen.driverservice.dto.request.DriverRequest
import com.software.modsen.driverservice.dto.response.CarResponse
import com.software.modsen.driverservice.dto.response.DriverResponse
import com.software.modsen.driverservice.dto.response.DriverWithCarResponse
import com.software.modsen.driverservice.model.Sex
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
        val driverRequest = defaultDriverRequest
        val expectedDriverResponse = defaultDriverResponse
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
        val expectedDriverResponse = defaultDriverResponseWithCar
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
        val updatedDriver = defaultUpdatedDriverRequest
        val expectedDriverResponse = defaultUpdatedDriver
        val defaultDriver = defaultDriverRequest
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
        val expectedDriverResponseList = listOf(defaultDriverResponse)
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
            content = objectMapper.writeValueAsString(defaultCarRequest)
        }
    }

    companion object {
        private const val DEFAULT_ID = 1L
        private const val DEFAULT_CAR_ID = 1L

        private const val DEFAULT_LICENSE_PLATE = "1234AB"
        private const val DEFAULT_MODEL = "Tesla Model S"
        private const val DEFAULT_YEAR = 2022
        private const val DEFAULT_COLOR = "Black"

        private const val DEFAULT_NAME = "Igor"
        private const val DEFAULT_EMAIL = "uralskipelmen@mail.ru"
        private const val DEFAULT_PHONE = "1234567890"
        private const val DEFAULT_SEX = "M"

        private const val DEFAULT_UPDATED_EMAIL = "comedy@mail.ru"
        private const val DEFAULT_UPDATED_PHONE = "0987654321"

        val defaultCarRequest = CarRequest(
            model = DEFAULT_MODEL,
            year = DEFAULT_YEAR,
            licensePlate = DEFAULT_LICENSE_PLATE,
            color = DEFAULT_COLOR
        )

        val defaultCarResponse = CarResponse(
            carId = DEFAULT_CAR_ID,
            model = DEFAULT_MODEL,
            year = DEFAULT_YEAR,
            licensePlate = DEFAULT_LICENSE_PLATE,
            color = DEFAULT_COLOR
        )

        val defaultUpdatedDriver = DriverResponse(
                driverId = DEFAULT_ID,
                name = DEFAULT_NAME,
                email = DEFAULT_UPDATED_EMAIL,
                phone = DEFAULT_UPDATED_PHONE,
                sex = Sex.M
            )

        val defaultDriverRequest = DriverRequest(
                name = DEFAULT_NAME,
                email = DEFAULT_EMAIL,
                phone = DEFAULT_PHONE,
                sex = DEFAULT_SEX,
                carId = DEFAULT_CAR_ID
            )

        val defaultUpdatedDriverRequest = DriverRequest(
                name = DEFAULT_NAME,
                email = DEFAULT_UPDATED_EMAIL,
                phone = DEFAULT_UPDATED_PHONE,
                sex = DEFAULT_SEX,
                carId = DEFAULT_CAR_ID
            )

        val defaultDriverResponse = DriverResponse(
                driverId = DEFAULT_ID,
                name = DEFAULT_NAME,
                email = DEFAULT_EMAIL,
                phone = DEFAULT_PHONE,
                sex = Sex.M
            )

        val defaultDriverResponseWithCar = DriverWithCarResponse(
                driverId = DEFAULT_ID,
                name = DEFAULT_NAME,
                email = DEFAULT_EMAIL,
                phone = DEFAULT_PHONE,
                sex = Sex.M,
                car = defaultCarResponse
            )
    }
}
