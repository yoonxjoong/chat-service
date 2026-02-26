package com.example.chatservice

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.YearMonth

@RestController
@RequestMapping("/api/swimming")
class SwimmingRecordController(
    private val swimmingRecordRepository: SwimmingRecordRepository,
    private val memberRepository: MemberRepository
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
            SwimmingRecordDto(it.id, it.date, it.distance, it.duration, it.memo, it.imageUrl)
        }
    }

    @PostMapping("/record")
    fun saveRecord(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody dto: SwimmingRecordDto
    ): Map<String, String> {
        val member = memberRepository.findByUsername(userDetails.username) ?: throw RuntimeException("User not found")
        val existingRecord = swimmingRecordRepository.findByMemberAndDate(member, dto.date)
        
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
                distance = dto.distance,
                duration = dto.duration,
                memo = dto.memo,
                imageUrl = dto.imageUrl
            )
            swimmingRecordRepository.save(record)
        }
        
        return mapOf("message" to "기록이 저장되었습니다.")
    }

    @GetMapping("/stats/summary")
    fun getStatsSummary(@AuthenticationPrincipal userDetails: UserDetails): Map<String, Any> {
        val member = memberRepository.findByUsername(userDetails.username) ?: throw RuntimeException("User not found")
        
        // 최근 7일 데이터
        val last7Days = (0..6).map { LocalDate.now().minusDays(it.toLong()) }.reversed()
        val weeklyData = last7Days.map { date ->
            val record = swimmingRecordRepository.findByMemberAndDate(member, date)
            mapOf("date" to date.toString(), "distance" to (record?.distance ?: 0))
        }

        // 최근 6개월 데이터
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
}

data class SwimmingRecordDto(
    val id: Long? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate = LocalDate.now(),
    val distance: Int = 0,
    val duration: Int = 0,
    val memo: String? = "",
    val imageUrl: String? = null
)
