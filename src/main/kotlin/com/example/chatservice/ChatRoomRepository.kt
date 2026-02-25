package com.example.chatservice

import org.springframework.stereotype.Repository

@Repository
class ChatRoomRepository {
    private val chatRooms = LinkedHashMap<String, ChatRoom>()

    fun findAllRooms(): List<ChatRoom> {
        return chatRooms.values.reversed()
    }

    fun findRoomById(id: String): ChatRoom? {
        return chatRooms[id]
    }

    fun createChatRoom(name: String): ChatRoom {
        val room = ChatRoom(name = name)
        chatRooms[room.roomId] = room
        return room
    }
}