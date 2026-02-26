package com.example.chatservice

import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class ChatController(
    private val redisPublisher: RedisPublisher,
    private val channelTopic: ChannelTopic,
    private val chatRoomRepository: ChatRoomRepository
) {
    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    fun message(message: ChatMessage, @Header("simpSessionAttributes") attributes: MutableMap<String, Any>) {
        var updatedMessage = message

        if (message.type == MessageType.ENTER) {
            val chatRoom = chatRoomRepository.findRoomById(message.roomId)
            if (chatRoom != null) {
                // 세션에 정보 저장 (연결 끊김 시 사용)
                attributes["roomId"] = message.roomId
                attributes["sender"] = message.sender
                
                chatRoomRepository.addUser(message.roomId, message.sender)
                updatedMessage = message.copy(
                    message = "${message.sender}님이 입장하셨습니다.",
                    userCount = chatRoomRepository.getUserCount(message.roomId)
                )
            } else {
                return
            }
        } else if (message.type == MessageType.QUIT) {
            chatRoomRepository.removeUser(message.roomId, message.sender)
            updatedMessage = message.copy(
                message = "${message.sender}님이 퇴장하셨습니다.",
                userCount = chatRoomRepository.getUserCount(message.roomId)
            )
        }
        
        redisPublisher.publish(channelTopic, updatedMessage)
    }
}