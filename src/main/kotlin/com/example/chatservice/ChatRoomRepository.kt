package com.example.chatservice

import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import jakarta.annotation.PostConstruct

@Repository
class ChatRoomRepository(private val redisTemplate: RedisTemplate<String, Any>) {

    // Redis Hash Key for ChatRoom objects
    private val CHAT_ROOMS = "CHAT_ROOM"
    // Redis Set Key prefix for User IDs in each room
    private val USER_COUNT_PREFIX = "USER_COUNT:"
    
    private lateinit var opsHashChatRoom: HashOperations<String, String, ChatRoom>

    @PostConstruct
    private fun init() {
        opsHashChatRoom = redisTemplate.opsForHash()
        
        // 고정 채널 목록 생성
        val defaultRooms = mapOf(
            "today-swim-room" to "🏊 오늘의 수영",
            "general-chat" to "💬 자유 수다",
            "swimming-tips" to "🛠 수영 꿀팁"
        )

        defaultRooms.forEach { (id, name) ->
            if (opsHashChatRoom.get(CHAT_ROOMS, id) == null) {
                val room = ChatRoom(roomId = id, name = name)
                opsHashChatRoom.put(CHAT_ROOMS, id, room)
            }
        }
    }

    fun findAllRooms(): List<ChatRoom> {
        val rooms = opsHashChatRoom.values(CHAT_ROOMS)
        // 각 방의 현재 인원수를 조회하여 세팅
        rooms.forEach { room ->
            room.userCount = getUserCount(room.roomId)
        }
        return rooms
    }

    fun findRoomById(id: String): ChatRoom? {
        val room = opsHashChatRoom.get(CHAT_ROOMS, id)
        room?.userCount = getUserCount(id)
        return room
    }

    /**
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
     */
    fun createChatRoom(name: String): ChatRoom {
        val chatRoom = ChatRoom(name = name)
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.roomId, chatRoom)
        return chatRoom
    }

    /**
     * 채팅방 삭제
     */
    fun deleteRoom(roomId: String) {
        opsHashChatRoom.delete(CHAT_ROOMS, roomId)
        redisTemplate.delete(USER_COUNT_PREFIX + roomId) // 인원수 데이터도 삭제
    }

    /**
     * 채팅방 인원수 조회
     */
    fun getUserCount(roomId: String): Long {
        return redisTemplate.opsForSet().size(USER_COUNT_PREFIX + roomId) ?: 0
    }

    /**
     * 채팅방에 유저 추가 (중복 방지를 위해 Set 사용)
     */
    fun addUser(roomId: String, userId: String) {
        redisTemplate.opsForSet().add(USER_COUNT_PREFIX + roomId, userId)
    }

    /**
     * 채팅방에서 유저 제거
     */
    fun removeUser(roomId: String, userId: String) {
        redisTemplate.opsForSet().remove(USER_COUNT_PREFIX + roomId, userId)
    }
}