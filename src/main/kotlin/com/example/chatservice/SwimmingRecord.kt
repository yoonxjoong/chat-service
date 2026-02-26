package com.example.chatservice

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "swimming_records")
class SwimmingRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member? = null,

    val date: LocalDate = LocalDate.now(),
    var distance: Int = 0,
    var duration: Int = 0,
    
    @Column(columnDefinition = "TEXT")
    var memo: String? = null,

    var imageUrl: String? = null
)
