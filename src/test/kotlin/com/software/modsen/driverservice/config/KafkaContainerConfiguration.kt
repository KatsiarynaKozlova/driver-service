package com.software.modsen.driverservice.config

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
object KafkaContainerConfiguration {
    @Container
    private val kafkaContainer = KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"))
        .apply { start() }

    @DynamicPropertySource
    fun kafkaProperties(registry: DynamicPropertyRegistry) {
        registry.add("spring.kafka.properties.bootstrap.servers"){ kafkaContainer::getBootstrapServers}
        registry.add("spring.kafka.producer.bootstrap.servers"){ kafkaContainer::getBootstrapServers}
    }
}
