package com.example.chatservice

import java.io.Serializable
import java.util.*

data class ChatRoom(
    val roomId: String = UUID.randomUUID().toString(),
    val name: String,
    var userCount: Long = 0 // 현재 접속자 수
) : Serializable
