package com.example.chatservice

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/mul-ot")
class MulOtRecordController(
    private val mulOtRecordRepository: MulOtRecordRepository,
    private val memberRepository: MemberRepository
) {

    @GetMapping("/records")
    fun getRecords(@AuthenticationPrincipal userDetails: UserDetails): List<MulOtRecordDto> {
        val member = memberRepository.findByUsername(userDetails.username) ?: throw RuntimeException("User not found")
        return mulOtRecordRepository.findAllByMemberOrderByDateDesc(member).map {
            MulOtRecordDto(it.id, it.date, it.imageUrl, it.memo)
        }
    }

    @PostMapping("/record")
    fun saveRecord(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody dto: MulOtRecordDto
    ): Map<String, String> {
        val member = memberRepository.findByUsername(userDetails.username) ?: throw RuntimeException("User not found")
        val record = MulOtRecord(
            member = member,
            date = dto.date ?: LocalDate.now(),
            imageUrl = dto.imageUrl,
            memo = dto.memo
        )
        mulOtRecordRepository.save(record)
        return mapOf("message" to "물옷 기록이 저장되었습니다.")
    }

    @DeleteMapping("/record/{id}")
    fun deleteRecord(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable id: Long
    ): Map<String, String> {
        val member = memberRepository.findByUsername(userDetails.username) ?: throw RuntimeException("User not found")
        val record = mulOtRecordRepository.findById(id).orElseThrow { RuntimeException("Record not found") }
        
        if (record.member?.id != member.id) {
            throw RuntimeException("Access denied")
        }
        
        mulOtRecordRepository.delete(record)
        return mapOf("message" to "기록이 삭제되었습니다.")
    }
}

data class MulOtRecordDto(
    val id: Long? = null,
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate? = null,
    val imageUrl: String? = null,
    val memo: String? = null
)
