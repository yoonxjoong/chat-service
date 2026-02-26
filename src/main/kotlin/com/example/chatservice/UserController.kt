package com.example.chatservice

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val memberRepository: MemberRepository) {
    @GetMapping("/api/user/me")
    fun me(@AuthenticationPrincipal userDetails: UserDetails): Map<String, String> {
        val member = memberRepository.findByUsername(userDetails.username)
        return mapOf(
            "username" to (member?.username ?: ""),
            "nickname" to (member?.nickname ?: userDetails.username)
        )
    }
}
