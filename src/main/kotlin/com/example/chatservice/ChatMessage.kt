package com.example.chatservice

// 메시지 모델 : 채팅방에서 오가는 데이터의 표준 규격
data class ChatMessage(
    val type: MessageType = MessageType.TALK,
    val roomId: String = "",
    val sender: String = "",    // 닉네임
    val senderId: String = "",  // 로그인 ID (고유 식별자)
    val message: String = "",
    val userCount: Long = 0     // 현재 채팅방 인원수
)
