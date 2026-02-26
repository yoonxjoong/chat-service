package com.example.chatservice

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SwimmingRecordTest {

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var swimmingRecordRepository: SwimmingRecordRepository

    private lateinit var mockMvc: MockMvc

    companion object {
        @Container
        val redis = GenericContainer("redis:latest").withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redis.host }
            registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }
        }
    }

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    @WithMockUser(username = "test1")
    fun `회원 탈퇴 및 연관 데이터 자동 삭제 테스트`() {
        // 1. 테스트 유저 및 기록 생성
        var member = memberRepository.findByUsername("test1")
        if (member == null) {
            member = memberRepository.save(Member(username = "test1", password = "password", nickname = "테스터1"))
        }
        
        swimmingRecordRepository.save(SwimmingRecord(member = member, distance = 1000, duration = 30))

        // 2. 기록이 존재하는지 확인
        assertThat(swimmingRecordRepository.findAllByMemberAndDateBetween(member!!, LocalDate.MIN, LocalDate.MAX)).isNotEmpty

        // 3. 회원 탈퇴 요청
        mockMvc.perform(delete("/api/user/withdraw")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)

        // 4. 회원 및 기록이 모두 삭제되었는지 확인
        assertThat(memberRepository.findByUsername("test1")).isNull()
        val records = swimmingRecordRepository.findAll()
        assertThat(records.none { it.member?.username == "test1" }).isTrue()
    }
}
