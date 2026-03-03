package com.example.chatservice.record

import org.springframework.data.jpa.repository.JpaRepository
import com.example.chatservice.member.Member
import java.time.LocalDate

interface SwimmingRecordRepository : JpaRepository<SwimmingRecord, Long> {
    fun findAllByMemberAndDateBetween(member: Member, start: LocalDate, end: LocalDate): List<SwimmingRecord>
    fun findByMemberAndDate(member: Member, date: LocalDate): SwimmingRecord?
    fun findAllByMemberAndDate(member: Member, date: LocalDate): List<SwimmingRecord>
    fun deleteAllByMember(member: Member) // 회원 탈퇴 시 모든 기록 삭제를 위해 추가
}
