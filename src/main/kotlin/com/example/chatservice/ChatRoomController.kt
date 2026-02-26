package com.example.chatservice

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatRoomController(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository
) {

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

    // 특정 채팅방의 메시지 내역 조회
    @GetMapping("/room/{roomId}/messages")
    fun roomMessages(@PathVariable roomId: String): List<ChatMessageEntity> {
        return chatMessageRepository.findAllByRoomIdOrderByCreatedAtAsc(roomId)
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    fun roomInfo(@PathVariable roomId: String): ChatRoom? {
        return chatRoomRepository.findRoomById(roomId)
    }

    // 채팅방 삭제
    @DeleteMapping("/room/{roomId}")
    fun deleteRoom(@PathVariable roomId: String) {
        chatRoomRepository.deleteRoom(roomId)
    }
}
