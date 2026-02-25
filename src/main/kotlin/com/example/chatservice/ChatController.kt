package com.example.chatservice

import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class ChatController(
    private val redisPublisher: RedisPublisher,
    private val channelTopic: ChannelTopic
) {
    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    fun message(message: ChatMessage) {
        var updatedMessage = message

        if (message.type == MessageType.ENTER) {
            updatedMessage = message.copy(message = "${message.sender}님이 입장하셨습니다.")
        }
        
        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        redisPublisher.publish(channelTopic, updatedMessage)
    }
}