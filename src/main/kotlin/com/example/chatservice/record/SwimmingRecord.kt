package com.example.chatservice.record

import jakarta.persistence.*
import java.time.LocalDate
import com.example.chatservice.member.Member

enum class StrokeType {
    FREE,   // 자유형
    BACK,   // 배영
    BREAST, // 평영
    FLY,    // 접영
    IM,     // 개인혼영
    KICK,   // 킥판/훈련
    MIXED   // 혼합/기타
}

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
    
    @Enumerated(EnumType.STRING)
    var strokeType: StrokeType = StrokeType.FREE,
    
    var distance: Int = 0, // 미터 단위
    var duration: Int = 0, // 초 단위
    
    @Column(columnDefinition = "TEXT")
    var memo: String? = null,

    var imageUrl: String? = null
)
