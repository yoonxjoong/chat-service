package com.example.chatservice.record

import org.springframework.data.jpa.repository.JpaRepository
import com.example.chatservice.member.Member
import org.springframework.stereotype.Repository

@Repository
interface MulOtRecordRepository : JpaRepository<MulOtRecord, Long> {
    fun findAllByMemberOrderByDateDesc(member: Member): List<MulOtRecord>
    fun deleteAllByMember(member: Member)
}
