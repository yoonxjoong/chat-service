package com.example.chatservice

import org.springframework.context.event.EventListener
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class WebSocketEventListener(
    private val redisPublisher: RedisPublisher,
    private val channelTopic: ChannelTopic,
    private val chatRoomRepository: ChatRoomRepository
) {

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        
        // 세션 속성에서 저장해둔 정보 추출
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val sender = headerAccessor.sessionAttributes?.get("sender") as? String
        val senderId = headerAccessor.sessionAttributes?.get("senderId") as? String

        if (roomId != null && sender != null && senderId != null) {
            println("Disconnected : $sender($senderId) from $roomId")

            // 리포지토리에서 유저 제거 (ID 기준)
            chatRoomRepository.removeUser(roomId, senderId)
            
            // 퇴장 메시지 브로드캐스트
            val chatMessage = ChatMessage(
                type = MessageType.QUIT,
                roomId = roomId,
                sender = sender,
                senderId = senderId,
                message = "${sender}님이 퇴장하셨습니다.",
                userCount = chatRoomRepository.getUserCount(roomId)
            )
            
            redisPublisher.publish(channelTopic, chatMessage)
        }
    }
}
