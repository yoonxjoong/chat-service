package com.example.chatservice

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessageEntity, Long> {
    fun findAllByRoomIdOrderByCreatedAtAsc(roomId: String): List<ChatMessageEntity>
}
