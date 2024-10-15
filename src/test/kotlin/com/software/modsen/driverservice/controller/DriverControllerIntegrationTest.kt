package com.software.modsen.driverservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.software.modsen.driverservice.config.DatabaseContainerConfiguration
import com.software.modsen.driverservice.config.KafkaContainerConfiguration
import com.software.modsen.driverservice.dto.request.CarRequest
import com.software.modsen.driverservice.dto.request.DriverRequest
import com.software.modsen.driverservice.dto.response.DriverResponse
import com.software.modsen.driverservice.model.Sex
import com.software.modsen.driverservice.util.ExceptionMessages
import org.junit.jupiter.api.Test
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
    fun `should return not found`() {
        mockMvc.get("/drivers/{id}", DEFAULT_NOT_EXISTING_ID)
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.message") {
                    value(
                        ExceptionMessages.DRIVER_NOT_FOUND_EXCEPTION.format(DEFAULT_NOT_EXISTING_ID)
                    )
                }
            }
    }

    @Test
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
                jsonPath("$.name") { value(expectedDriverResponse.name) }
                jsonPath("$.email") { value(expectedDriverResponse.email) }
                jsonPath("$.phone") { value(expectedDriverResponse.phone) }
            }
    }

    @Test
    fun `should update driver by id and return updated`() {
        val updatedDriver = defaultUpdatedDriverRequest
        val expectedDriverResponse = defaultUpdatedDriver
        val defaultDriver = defaultDriverRequest
        setupCar()
        mockMvc.post("/drivers")
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updatedDriver)
        }
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
    }

    @Test
    fun `should return list of drivers`() {
        mockMvc.get("/drivers")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    RequestPredicates.contentType(MediaType.APPLICATION_JSON)
                }
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
        private const val DEFAULT_NOT_EXISTING_ID = 0L
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
    }
}
