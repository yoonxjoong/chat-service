package com.example.chatservice

import java.util.*

data class ChatRoom(
    val roomId: String = UUID.randomUUID().toString(),
    val name: String,
    val sessions: MutableSet<String> = mutableSetOf() // 세션 관리를 위한 구조
)
