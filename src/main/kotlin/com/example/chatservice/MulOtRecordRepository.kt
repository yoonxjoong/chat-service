package com.example.chatservice

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MulOtRecordRepository : JpaRepository<MulOtRecord, Long> {
    fun findAllByMemberOrderByDateDesc(member: Member): List<MulOtRecord>
    fun deleteAllByMember(member: Member)
}
