package com.example.chatservice

import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/member")
class MemberController(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // 휴대폰 번호 기반 로그인 (인증번호 방식)
    @PostMapping("/login/phone")
    fun loginByPhone(
        @RequestBody request: PhoneLoginRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        // 1. 인증번호 검증 (하드코딩 1234)
        if (request.code != "1234") {
            return ResponseEntity.status(401).body(mapOf("message" to "인증번호가 일치하지 않습니다."))
        }

        // 2. 사용자 조회 또는 생성
        var member = memberRepository.findByUsername(request.phoneNumber)
        if (member == null) {
            // 신규 유저 자동 가입 (랜덤 닉네임 부여)
            val randomId = (1000..9999).random()
            member = Member(
                username = request.phoneNumber,
                nickname = "수영인_$randomId",
                password = passwordEncoder.encode("PHONE_USER") 
            )
            memberRepository.save(member)
        }

        // 3. 스프링 시큐리티 수동 인증 처리
        // Principal을 String이 아닌 UserDetails 객체로 설정해야 @AuthenticationPrincipal이 작동함
        val userDetails = User.builder()
            .username(member.username)
            .password("") // 비밀번호 인증은 이미 마침
            .authorities("ROLE_USER")
            .build()

        val auth = UsernamePasswordAuthenticationToken(
            userDetails, 
            null, 
            userDetails.authorities
        )
        SecurityContextHolder.getContext().authentication = auth
        
        // 세션에 인증 정보 저장
        val session = servletRequest.getSession(true)
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext())

        return ResponseEntity.ok(mapOf("message" to "로그인 성공", "nickname" to member.nickname))
    }

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

data class PhoneLoginRequest(
    val phoneNumber: String,
    val code: String
)
