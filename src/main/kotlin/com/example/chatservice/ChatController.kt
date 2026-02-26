package com.example.chatservice

import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class ChatController(
    private val redisPublisher: RedisPublisher,
    private val kafkaProducer: KafkaProducer,
    private val channelTopic: ChannelTopic,
    private val chatRoomRepository: ChatRoomRepository
) {
    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    fun message(message: ChatMessage, @Header("simpSessionAttributes") attributes: MutableMap<String, Any>, principal: java.security.Principal) {
        val loginId = principal.name // 고유 ID
        var updatedMessage = message.copy(senderId = loginId)

        if (message.type == MessageType.ENTER) {
            val chatRoom = chatRoomRepository.findRoomById(message.roomId)
            if (chatRoom != null) {
                attributes["roomId"] = message.roomId
                attributes["sender"] = message.sender
                attributes["senderId"] = loginId
                
                chatRoomRepository.addUser(message.roomId, loginId) // ID로 관리
                updatedMessage = updatedMessage.copy(
                    message = "${message.sender}님이 입장하셨습니다.",
                    userCount = chatRoomRepository.getUserCount(message.roomId)
                )
            } else {
                return
            }
        } else if (message.type == MessageType.QUIT) {
            chatRoomRepository.removeUser(message.roomId, loginId)
            updatedMessage = updatedMessage.copy(
                message = "${message.sender}님이 퇴장하셨습니다.",
                userCount = chatRoomRepository.getUserCount(message.roomId)
            )
        }
        
        // 실시간 전달을 위해 Redis로 발행
        redisPublisher.publish(channelTopic, updatedMessage)
        
        // 비동기 저장을 위해 Kafka로 전송 (TALK 타입만 전송하거나 전체 전송 가능)
        if (updatedMessage.type == MessageType.TALK) {
            kafkaProducer.send(updatedMessage)
        }
    }
}