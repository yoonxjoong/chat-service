package com.example.chatservice

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatRoomController(private val chatRoomRepository: ChatRoomRepository) {

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    fun rooms(): List<ChatRoom> {
        return chatRoomRepository.findAllRooms()
    }

    // 채팅방 생성
    @PostMapping("/room")
    fun createRoom(@RequestParam name: String): ChatRoom {
        return chatRoomRepository.createChatRoom(name)
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    fun roomInfo(@PathVariable roomId: String): ChatRoom? {
        return chatRoomRepository.findRoomById(roomId)
    }
}