package com.example.chatservice

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_messages")
class ChatMessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val type: String = "",
    val roomId: String = "",
    val sender: String = "",
    val senderId: String = "",
    
    @Column(columnDefinition = "TEXT")
    val message: String = "",
    
    val createdAt: LocalDateTime = LocalDateTime.now()
)
