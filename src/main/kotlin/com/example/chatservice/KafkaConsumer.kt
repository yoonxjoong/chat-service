package com.example.chatservice

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaConsumer(
    private val chatMessageDbRepository: ChatMessageRepository
) {
    @KafkaListener(topics = ["chat-messages"], groupId = "chat-group")
    fun consume(message: ChatMessage) {
        // TALK 메시지만 저장 (ENTER/QUIT은 선택사항)
        if (message.type == MessageType.TALK) {
            val entity = ChatMessageEntity(
                type = message.type.name,
                roomId = message.roomId,
                sender = message.sender,
                message = message.message
            )
            chatMessageDbRepository.save(entity)
            println("Message saved to DB: ${message.message}")
        }
    }
}
