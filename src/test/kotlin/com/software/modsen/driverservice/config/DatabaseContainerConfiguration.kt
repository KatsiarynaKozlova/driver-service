package com.software.modsen.driverservice.config

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
object DatabaseContainerConfiguration {
    @Container
    private val postgreSQL = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:latest"))
        .apply {
            withDatabaseName("test_db")
            withUsername("test")
            withPassword("test")
            start()
        }

    @DynamicPropertySource
    fun configureProperties(registry: DynamicPropertyRegistry) {
        with(registry) {
            add("spring.datasource.url") { postgreSQL.jdbcUrl }
            add("spring.datasource.username") { postgreSQL.username }
            add("spring.datasource.password") { postgreSQL.password }
            add("spring.datasource.driver-class-name") { postgreSQL.driverClassName }
        }
    }
}