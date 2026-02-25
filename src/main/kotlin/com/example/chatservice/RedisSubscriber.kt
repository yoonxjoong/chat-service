package com.example.chatservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service

@Service
class RedisSubscriber(
    private val objectMapper: ObjectMapper,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val messagingTemplate: SimpMessageSendingOperations
) : MessageListener {

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
     */
    override fun onMessage(message: Message, pattern: ByteArray?) {
        try {
            // redis에서 발행된 데이터를 받아 deserialize
            val publishMessage = redisTemplate.stringSerializer.deserialize(message.body)
            
            // ChatMessage 객채로 맵핑
            val roomMessage = objectMapper.readValue(publishMessage, ChatMessage::class.java)
            
            // Websocket 구독자에게 채팅 메시지 Send
            messagingTemplate.convertAndSend("/sub/chat/room/${roomMessage.roomId}", roomMessage)
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}
