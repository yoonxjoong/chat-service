package com.example.chatservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
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

    @Autowired
    private lateinit var objectMapper: ObjectMapper

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
        swimmingRecordRepository.deleteAll()
        memberRepository.deleteAll()
    }

    @Test
    @WithMockUser(username = "testuser")
    fun `다양한 영법 등록 조회 및 삭제 테스트`() {
        val member = memberRepository.save(Member(username = "testuser", password = "password", nickname = "물개"))
        val today = LocalDate.now()

        val freeDto = SwimmingRecordDto(date = today, strokeType = StrokeType.FREE, distance = 500, duration = 15)
        val backDto = SwimmingRecordDto(date = today, strokeType = StrokeType.BACK, distance = 300, duration = 10)

        mockMvc.perform(post("/api/swimming/record")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(freeDto)))
            .andExpect(status().isOk)

        mockMvc.perform(post("/api/swimming/record")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(backDto)))
            .andExpect(status().isOk)

        val records = swimmingRecordRepository.findAllByMemberAndDate(member, today)
        assertThat(records).hasSize(2)
        assertThat(records.map { it.strokeType }).containsExactlyInAnyOrder(StrokeType.FREE, StrokeType.BACK)

        val backRecordId = records.find { it.strokeType == StrokeType.BACK }?.id!!
        mockMvc.perform(delete("/api/swimming/record/$backRecordId"))
            .andExpect(status().isOk)

        val remainingRecords = swimmingRecordRepository.findAllByMemberAndDate(member, today)
        assertThat(remainingRecords).hasSize(1)
        assertThat(remainingRecords[0].strokeType).isEqualTo(StrokeType.FREE)
    }

    @Test
    @WithMockUser(username = "withdraw_user")
    fun `회원 탈퇴 및 연관 데이터 자동 삭제 테스트`() {
        val member = memberRepository.save(Member(username = "withdraw_user", password = "password", nickname = "탈퇴자"))
        swimmingRecordRepository.save(SwimmingRecord(member = member, distance = 1000, duration = 30))

        // PostgreSQL 날짜 범위를 고려하여 현실적인 날짜로 수정
        val startDate = LocalDate.of(2000, 1, 1)
        val endDate = LocalDate.of(2100, 12, 31)
        assertThat(swimmingRecordRepository.findAllByMemberAndDateBetween(member, startDate, endDate)).isNotEmpty

        mockMvc.perform(delete("/api/user/withdraw"))
            .andExpect(status().isOk)

        assertThat(memberRepository.findByUsername("withdraw_user")).isNull()
        val records = swimmingRecordRepository.findAll()
        assertThat(records.none { it.member?.username == "withdraw_user" }).isTrue()
    }
}
