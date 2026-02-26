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
    
    val password: String = "",
    
    val nickname: String = ""
)
