package com.example.chatservice

import jakarta.persistence.EntityManager
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(
    private val memberRepository: MemberRepository,
    private val swimmingRecordRepository: SwimmingRecordRepository,
    private val mulOtRecordRepository: MulOtRecordRepository,
    private val entityManager: EntityManager
) {

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal userDetails: UserDetails?): ResponseEntity<Map<String, String?>> {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(mapOf("message" to "Not logged in"))
        }
        val member = memberRepository.findByUsername(userDetails.username)
        return ResponseEntity.ok(mapOf(
            "username" to (member?.username ?: ""),
            "nickname" to (member?.nickname ?: userDetails.username),
            "profileImageUrl" to member?.profileImageUrl,
            "distanceUnit" to (member?.distanceUnit ?: "METER")
        ))
    }

    @PutMapping("/profile")
    fun updateProfile(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody profileDto: ProfileUpdateDto
    ): Map<String, String> {
        val member = memberRepository.findByUsername(userDetails.username)
            ?: throw RuntimeException("User not found")
        
        member.nickname = profileDto.nickname
        member.profileImageUrl = profileDto.profileImageUrl
        if (profileDto.distanceUnit != null) {
            member.distanceUnit = profileDto.distanceUnit
        }
        memberRepository.save(member)
        
        return mapOf("message" to "프로필이 수정되었습니다.")
    }

    @DeleteMapping("/withdraw")
    @Transactional
    fun withdraw(
        @AuthenticationPrincipal userDetails: UserDetails,
        request: HttpServletRequest
    ): Map<String, String> {
        val member = memberRepository.findByUsername(userDetails.username)
            ?: throw RuntimeException("User not found")
        
        // 1. 수영 기록 삭제
        swimmingRecordRepository.deleteAllByMember(member)

        // 2. 물옷 기록 삭제
        mulOtRecordRepository.deleteAllByMember(member)

        // 3. 보안 컨텍스트 로그아웃
        val auth = org.springframework.security.core.context.SecurityContextHolder.getContext().authentication
        if (auth != null) {
            SecurityContextLogoutHandler().logout(request, null, auth)
        }

        // 3. 회원 삭제
        memberRepository.delete(member)
        
        return mapOf("message" to "회원 탈퇴가 완료되었습니다.")
    }
}

data class ProfileUpdateDto(
    val nickname: String,
    val profileImageUrl: String?,
    val distanceUnit: String?
)
