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
            SwimmingRecordDto(it.id, it.date, it.distance, it.duration, it.memo)
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
            swimmingRecordRepository.save(existingRecord)
        } else {
            val record = SwimmingRecord(
                member = member,
                date = dto.date,
                distance = dto.distance,
                duration = dto.duration,
                memo = dto.memo
            )
            swimmingRecordRepository.save(record)
        }
        
        return mapOf("message" to "기록이 저장되었습니다.")
    }
}

data class SwimmingRecordDto(
    val id: Long? = null,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate = LocalDate.now(),
    
    val distance: Int = 0,
    val duration: Int = 0,
    val memo: String? = ""
)
