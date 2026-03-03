package com.example.chatservice.record

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.YearMonth
import com.example.chatservice.member.MemberRepository
import com.example.chatservice.chat.RedisPublisher
import com.example.chatservice.chat.ChatMessage
import com.example.chatservice.chat.MessageType

@RestController
@RequestMapping("/api/swimming")
class SwimmingRecordController(
    private val swimmingRecordRepository: SwimmingRecordRepository,
    private val memberRepository: MemberRepository,
    private val redisPublisher: RedisPublisher,
    private val channelTopic: org.springframework.data.redis.listener.ChannelTopic
) {

    @GetMapping("/records")
    fun getRecords(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestParam year: Int,
        @RequestParam month: Int
    ): List<SwimmingRecordDto> {
        val member = memberRepository.findByUsername(userDetails.username) ?: throw RuntimeException("User not found")
        val start = YearMonth.of(year, month).atDay(1)
        val end = YearMonth.of(year, month).atEndOfMonth()
        
        return swimmingRecordRepository.findAllByMemberAndDateBetween(member, start, end).map {
            SwimmingRecordDto(it.id, it.date, it.strokeType, it.distance, it.duration, it.memo, it.imageUrl)
        }
    }

    @PostMapping("/record")
    fun saveRecord(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody dto: SwimmingRecordDto
    ): Map<String, String> {
        val member = memberRepository.findByUsername(userDetails.username) ?: throw RuntimeException("User not found")
        
        val existingRecord = swimmingRecordRepository.findAllByMemberAndDate(member, dto.date)
            .find { it.strokeType == dto.strokeType }
        
        if (existingRecord != null) {
            existingRecord.distance = dto.distance
            existingRecord.duration = dto.duration
            existingRecord.memo = dto.memo
            existingRecord.imageUrl = dto.imageUrl
            swimmingRecordRepository.save(existingRecord)
        } else {
            val record = SwimmingRecord(
                member = member,
                date = dto.date,
                strokeType = dto.strokeType,
                distance = dto.distance,
                duration = dto.duration,
                memo = dto.memo,
                imageUrl = dto.imageUrl
            )
            swimmingRecordRepository.save(record)
        }

        if (dto.date == LocalDate.now()) {
            val unit = if (member.distanceUnit == "YARD") "yd" else "m"
            val displayDistance = if (member.distanceUnit == "YARD") Math.round(dto.distance * 1.09361) else dto.distance
            
            val strokeName = when(dto.strokeType) {
                StrokeType.FREE -> "자유형"
                StrokeType.BACK -> "배영"
                StrokeType.BREAST -> "평영"
                StrokeType.FLY -> "접영"
                StrokeType.IM -> "개인혼영"
                StrokeType.KICK -> "발차기"
                else -> "수영"
            }

            val systemMsg = ChatMessage(
                type = MessageType.TALK,
                roomId = "today-swim-room",
                sender = "시스템",
                senderId = "system",
                message = "${member.nickname}님이 오늘 ${strokeName} ${displayDistance}${unit} 수영을 완료했습니다! 🏊‍♂️"
            )
            redisPublisher.publish(channelTopic, systemMsg)
        }
        
        return mapOf("message" to "기록이 저장되었습니다.")
    }

    @GetMapping("/stats/summary")
    fun getStatsSummary(@AuthenticationPrincipal userDetails: UserDetails): Map<String, Any> {
        val member = memberRepository.findByUsername(userDetails.username) ?: throw RuntimeException("User not found")
        
        val last7Days = (0..6).map { LocalDate.now().minusDays(it.toLong()) }.reversed()
        val weeklyData = last7Days.map { date ->
            val totalDistance = swimmingRecordRepository.findAllByMemberAndDate(member, date).sumOf { it.distance }
            mapOf("date" to date.toString(), "distance" to totalDistance)
        }

        val last6Months = (0..5).map { YearMonth.now().minusMonths(it.toLong()) }.reversed()
        val monthlyData = last6Months.map { ym ->
            val start = ym.atDay(1)
            val end = ym.atEndOfMonth()
            val totalDistance = swimmingRecordRepository.findAllByMemberAndDateBetween(member, start, end).sumOf { it.distance }
            mapOf("month" to ym.toString(), "distance" to totalDistance)
        }

        return mapOf(
            "weekly" to weeklyData,
            "monthly" to monthlyData
        )
    }

    @DeleteMapping("/record/{id}")
    fun deleteRecord(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable id: Long
    ): Map<String, String> {
        val member = memberRepository.findByUsername(userDetails.username) ?: throw RuntimeException("User not found")
        val record = swimmingRecordRepository.findById(id).orElseThrow { RuntimeException("Record not found") }
        
        if (record.member?.id != member.id) {
            throw RuntimeException("Access denied")
        }
        
        swimmingRecordRepository.delete(record)
        return mapOf("message" to "기록이 삭제되었습니다.")
    }
}

data class SwimmingRecordDto(
    val id: Long? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate = LocalDate.now(),
    val strokeType: StrokeType = StrokeType.FREE,
    val distance: Int = 0,
    val duration: Int = 0,
    val memo: String? = "",
    val imageUrl: String? = null
)
