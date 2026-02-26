package com.example.chatservice

import jakarta.persistence.*

@Entity
@Table(name = "members")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true)
    val username: String = "",
    
    var password: String = "",
    
    var nickname: String = "",

    var profileImageUrl: String? = null,

    var distanceUnit: String = "METER",

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val swimmingRecords: MutableList<SwimmingRecord> = mutableListOf()
)
