package com.example.chatservice

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(private val memberRepository: MemberRepository) {

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal userDetails: UserDetails): Map<String, String?> {
        val member = memberRepository.findByUsername(userDetails.username)
        return mapOf(
            "username" to (member?.username ?: ""),
            "nickname" to (member?.nickname ?: userDetails.username),
            "profileImageUrl" to member?.profileImageUrl,
            "distanceUnit" to (member?.distanceUnit ?: "METER")
        )
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
        // 단위 설정 업데이트 추가
        if (profileDto.distanceUnit != null) {
            member.distanceUnit = profileDto.distanceUnit
        }
        memberRepository.save(member)
        
        return mapOf("message" to "프로필이 수정되었습니다.")
    }

    @DeleteMapping("/withdraw")
    fun withdraw(@AuthenticationPrincipal userDetails: UserDetails): Map<String, String> {
        val member = memberRepository.findByUsername(userDetails.username)
            ?: throw RuntimeException("User not found")
        
        memberRepository.delete(member)
        return mapOf("message" to "회원 탈퇴가 완료되었습니다.")
    }
}

data class ProfileUpdateDto(
    val nickname: String,
    val profileImageUrl: String?,
    val distanceUnit: String? // 추가
)
