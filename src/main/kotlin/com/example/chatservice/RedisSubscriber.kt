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

    override fun onMessage(message: Message, pattern: ByteArray?) {
        try {
            val publishMessage = redisTemplate.stringSerializer.deserialize(message.body)
            val roomMessage = objectMapper.readValue(publishMessage, ChatMessage::class.java)
            
            if (roomMessage != null) {
                // 1. 해당 채팅방 구독자에게 메시지 전송
                messagingTemplate.convertAndSend("/sub/chat/room/${roomMessage.roomId}", roomMessage)
                
                // 2. 글로벌 알림 토픽으로 전송 (알림 및 안읽은 메시지 체크용)
                if (roomMessage.type == MessageType.TALK) {
                    messagingTemplate.convertAndSend("/sub/chat/all/notifications", roomMessage)
                }
            }
        } catch (e: Exception) {
            println("RedisSubscriber Exception: ${e.message}")
        }
    }
}
