package com.example.chatservice

// 메시지 모델 : 채팅방에서 오가는 데이터의 표준 규격
data class ChatMessage(
    val type: MessageType,
    val roomId: String,
    val sender: String,
    val message: String = ""
)
