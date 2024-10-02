package com.software.modsen.driverservice.kafka.producer

import com.software.modsen.driverservice.dto.request.DriverForRating
import lombok.RequiredArgsConstructor
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class DriverProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    companion object {
        private const val KAFKA_TOPIC = "driver-ids-topic"
    }

    fun sendDriver(driver: DriverForRating) {
        val message = MessageBuilder
            .withPayload(driver)
            .setHeader(KafkaHeaders.TOPIC, KAFKA_TOPIC)
            .build();
        kafkaTemplate.send(message)
    }
}
