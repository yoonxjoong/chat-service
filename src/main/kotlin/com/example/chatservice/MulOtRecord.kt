package com.example.chatservice

import jakarta.persistence.*
import java.time.LocalDate

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
