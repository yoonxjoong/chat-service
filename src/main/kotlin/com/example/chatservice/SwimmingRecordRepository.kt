package com.example.chatservice

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface SwimmingRecordRepository : JpaRepository<SwimmingRecord, Long> {
    fun findAllByMemberAndDateBetween(member: Member, start: LocalDate, end: LocalDate): List<SwimmingRecord>
    fun findByMemberAndDate(member: Member, date: LocalDate): SwimmingRecord?
}
