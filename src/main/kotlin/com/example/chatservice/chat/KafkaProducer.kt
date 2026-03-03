package com.example.chatservice.chat

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, ChatMessage>
) {
    private val TOPIC = "chat-messages"

    fun send(message: ChatMessage) {
        kafkaTemplate.send(TOPIC, message)
    }
}
