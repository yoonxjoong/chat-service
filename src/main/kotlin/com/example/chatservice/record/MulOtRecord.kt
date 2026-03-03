package com.example.chatservice.record

import jakarta.persistence.*
import java.time.LocalDate
import com.example.chatservice.member.Member

@Entity
@Table(name = "mul_ot_records")
class MulOtRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member? = null,

    val date: LocalDate = LocalDate.now(),

    var imageUrl: String? = null,

    @Column(columnDefinition = "TEXT")
    var memo: String? = null
)
