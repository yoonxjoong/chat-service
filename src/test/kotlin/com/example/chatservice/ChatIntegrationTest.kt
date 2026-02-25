package com.example.chatservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.lang.reflect.Type
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ChatIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var stompClient: WebSocketStompClient

    companion object {
        @Container
        val redis = GenericContainer("redis:latest")
            .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }
        }
    }

    @BeforeEach
    fun setup() {
        stompClient = WebSocketStompClient(SockJsClient(listOf(WebSocketTransport(StandardWebSocketClient()))))
        val converter = MappingJackson2MessageConverter()
        converter.objectMapper = objectMapper.registerKotlinModule()
        stompClient.messageConverter = converter
    }

    @Test
    fun `메시지 송수신 테스트`() {
        val roomId = "test-room"
        val sender = "tester"
        val messageContent = "Hello, World!"
        
        val blockingQueue: BlockingQueue<ChatMessage> = LinkedBlockingDeque()

        val url = "ws://localhost:$port/ws-stomp"
        
        val session: StompSession = stompClient.connectAsync(url, object : StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS)

        // 구독
        session.subscribe("/sub/chat/room/$roomId", object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type = ChatMessage::class.java

            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                blockingQueue.offer(payload as ChatMessage)
            }
        })

        // 메시지 전송
        val chatMessage = ChatMessage(
            type = MessageType.TALK,
            roomId = roomId,
            sender = sender,
            message = messageContent
        )
        session.send("/pub/chat/message", chatMessage)

        // 결과 검증
        val receivedMessage = blockingQueue.poll(5, TimeUnit.SECONDS)
        
        assertThat(receivedMessage).isNotNull
        assertThat(receivedMessage?.roomId).isEqualTo(roomId)
        assertThat(receivedMessage?.sender).isEqualTo(sender)
        assertThat(receivedMessage?.message).isEqualTo(messageContent)
    }

    @Test
    fun `입장 메시지 테스트`() {
        val roomId = "enter-room"
        val sender = "new-user"
        
        val blockingQueue: BlockingQueue<ChatMessage> = LinkedBlockingDeque()

        val url = "ws://localhost:$port/ws-stomp"
        val session: StompSession = stompClient.connectAsync(url, object : StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS)

        session.subscribe("/sub/chat/room/$roomId", object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type = ChatMessage::class.java
            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                blockingQueue.offer(payload as ChatMessage)
            }
        })

        val enterMessage = ChatMessage(
            type = MessageType.ENTER,
            roomId = roomId,
            sender = sender
        )
        session.send("/pub/chat/message", enterMessage)

        val receivedMessage = blockingQueue.poll(5, TimeUnit.SECONDS)
        
        assertThat(receivedMessage).isNotNull
        assertThat(receivedMessage?.sender).isEqualTo(sender)
        assertThat(receivedMessage?.message).isEqualTo("${sender}님이 입장하셨습니다.")
    }
}
