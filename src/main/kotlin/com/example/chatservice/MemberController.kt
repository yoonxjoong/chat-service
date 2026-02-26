package com.example.chatservice

import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/member")
class MemberController(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // 아이디 중복 체크
    @GetMapping("/check-id")
    fun checkId(@RequestParam username: String): ResponseEntity<Map<String, Boolean>> {
        val exists = memberRepository.findByUsername(username) != null
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    // 회원가입 처리
    @PostMapping("/register")
    fun register(@RequestBody memberDto: MemberDto): ResponseEntity<Map<String, String>> {
        if (memberRepository.findByUsername(memberDto.username) != null) {
            return ResponseEntity.badRequest().body(mapOf("message" to "이미 존재하는 아이디입니다."))
        }

        val member = Member(
            username = memberDto.username,
            password = passwordEncoder.encode(memberDto.password),
            nickname = memberDto.nickname
        )
        memberRepository.save(member)
        return ResponseEntity.ok(mapOf("message" to "회원가입이 완료되었습니다."))
    }
}

data class MemberDto(
    val username: String,
    val password: String,
    val nickname: String
)
