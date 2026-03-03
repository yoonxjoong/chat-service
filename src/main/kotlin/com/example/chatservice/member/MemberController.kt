package com.example.chatservice.member

import org.springframework.http.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.HttpClientErrorException
import org.springframework.util.LinkedMultiValueMap
import jakarta.servlet.http.HttpServletRequest
import com.example.chatservice.record.SwimmingRecordRepository
import com.example.chatservice.record.MulOtRecordRepository

@RestController
@RequestMapping("/api/member")
class MemberController(
    private val memberRepository: MemberRepository,
    private val swimmingRecordRepository: SwimmingRecordRepository,
    private val mulOtRecordRepository: MulOtRecordRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val restTemplate = RestTemplate()
    private val KAKAO_ADMIN_KEY = "780dfc47bfb99d28bc1abc34b0bf0ff9"
    private val KAKAO_CLIENT_ID = "10370eb1fddb35728f39be1f5a057cb5"
    private val NAVER_CLIENT_ID = "yE_8k2KwVSQpt144laNn"
    private val NAVER_CLIENT_SECRET = "LvmhDhk5fj"

    // 카카오 로그인
    @PostMapping("/login/kakao")
    fun loginByKakao(
        @RequestBody request: KakaoLoginRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        val code = request.code ?: return ResponseEntity.badRequest().body(mapOf("message" to "인가 코드가 누락되었습니다."))
        val redirectUri = request.redirectUri ?: "http://localhost:8080/login"

        val tokenUrl = "https://kauth.kakao.com/oauth/token"
        val tokenHeaders = HttpHeaders()
        tokenHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        
        val tokenBody = LinkedMultiValueMap<String, String>()
        tokenBody.add("grant_type", "authorization_code")
        tokenBody.add("client_id", KAKAO_CLIENT_ID) 
        tokenBody.add("redirect_uri", redirectUri)
        tokenBody.add("code", code)

        return processSocialLogin(tokenUrl, tokenBody, tokenHeaders, "https://kapi.kakao.com/v2/user/me", "kakao", servletRequest)
    }

    // 네이버 로그인
    @PostMapping("/login/naver")
    fun loginByNaver(
        @RequestBody request: NaverLoginRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        val code = request.code ?: return ResponseEntity.badRequest().body(mapOf("message" to "인가 코드가 누락되었습니다."))
        val state = request.state ?: ""

        val tokenUrl = "https://nid.naver.com/oauth2.0/token"
        val tokenHeaders = HttpHeaders()
        
        val tokenBody = LinkedMultiValueMap<String, String>()
        tokenBody.add("grant_type", "authorization_code")
        tokenBody.add("client_id", NAVER_CLIENT_ID)
        tokenBody.add("client_secret", NAVER_CLIENT_SECRET)
        tokenBody.add("code", code)
        tokenBody.add("state", state)

        return processSocialLogin(tokenUrl, tokenBody, tokenHeaders, "https://openapi.naver.com/v1/nid/me", "naver", servletRequest)
    }

    private fun processSocialLogin(
        tokenUrl: String,
        tokenBody: LinkedMultiValueMap<String, String>,
        tokenHeaders: HttpHeaders,
        userInfoUrl: String,
        provider: String,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        return try {
            val tokenRequest = HttpEntity(tokenBody, tokenHeaders)
            val tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map::class.java)
            val accessToken = (tokenResponse.body as Map<*, *>)["access_token"] as String

            // 연동 해제를 위해 액세스 토큰을 세션에 저장
            servletRequest.getSession(true).setAttribute("${provider}_access_token", accessToken)

            val headers = HttpHeaders()
            headers.setBearerAuth(accessToken)
            val userInfoResponse = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                HttpEntity<String>(headers),
                Map::class.java
            )

            val body = userInfoResponse.body as Map<*, *>
            val (socialId, profileImage) = when (provider) {
                "kakao" -> {
                    val account = body["kakao_account"] as Map<*, *>
                    val profile = account["profile"] as Map<*, *>
                    Triple(body["id"].toString(), profile["profile_image_url"] as String?, null)
                }
                "naver" -> {
                    val response = body["response"] as Map<*, *>
                    Triple(response["id"] as String, response["profile_image"] as String?, null)
                }
                else -> Triple("", "", null)
            }

            var member = memberRepository.findByUsername("${provider}_$socialId")
            
            if (member == null) {
                // 신규 가입 시 무조건 "수영인_랜덤숫자" 부여
                member = Member(
                    username = "${provider}_$socialId",
                    nickname = "수영인_${(1000..9999).random()}",
                    password = passwordEncoder.encode("${provider.uppercase()}_USER"),
                    profileImageUrl = profileImage,
                    provider = provider
                )
                memberRepository.save(member)
            } else {
                // 프로필 이미지만 최신화 (닉네임은 유지)
                if (profileImage != null && member.profileImageUrl != profileImage) {
                    member.profileImageUrl = profileImage
                    memberRepository.save(member)
                }
            }

            val userDetails = User.builder().username(member.username).password("").authorities("ROLE_USER").build()
            val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            SecurityContextHolder.getContext().authentication = auth
            servletRequest.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext())

            ResponseEntity.ok(mapOf("message" to "로그인 성공", "nickname" to member.nickname))

        } catch (e: HttpClientErrorException) {
            ResponseEntity.status(e.statusCode).body(mapOf("message" to "$provider 오류: ${e.responseBodyAsString}"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "서버 오류: ${e.message}"))
        }
    }

    @PostMapping("/login/phone")
    fun loginByPhone(@RequestBody request: PhoneLoginRequest, servletRequest: HttpServletRequest): ResponseEntity<Map<String, String>> {
        if (request.code != "1234") return ResponseEntity.status(401).body(mapOf("message" to "인증번호 불일치"))
        var member = memberRepository.findByUsername(request.phoneNumber)
        if (member == null) {
            member = Member(username = request.phoneNumber, nickname = "수영인_${(1000..9999).random()}", password = passwordEncoder.encode("PHONE_USER"))
            memberRepository.save(member)
        }
        val userDetails = User.builder().username(member.username).password("").authorities("ROLE_USER").build()
        val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = auth
        servletRequest.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext())
        return ResponseEntity.ok(mapOf("message" to "로그인 성공", "nickname" to member.nickname))
    }

    @GetMapping("/check-id")
    fun checkId(@RequestParam username: String) = ResponseEntity.ok(mapOf("exists" to (memberRepository.findByUsername(username) != null)))

    // 소셜 연동 해제
    @PostMapping("/unlink")
    @Transactional
    fun unlinkSocialAccount(
        @AuthenticationPrincipal userDetails: UserDetails,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        println("Unlink request received for user: ${userDetails.username}")
        val member = memberRepository.findByUsername(userDetails.username)
            ?: run {
                println("Unlink failed: User not found in DB - ${userDetails.username}")
                return ResponseEntity.status(404).body(mapOf("message" to "사용자를 찾을 수 없습니다."))
            }

        println("Unlinking member: ${member.username}, Provider: ${member.provider}")
        if (member.provider == null) {
            println("Unlink failed: Member is not a social account - ${member.username}")
            return ResponseEntity.badRequest().body(mapOf("message" to "소셜 로그인 계정이 아닙니다."))
        }

        try {
            // 0. 카카오 연동 해제 처리 (어드민 키 이용)
            if (member.provider == "kakao") {
                val kakaoId = member.username.replace("kakao_", "")
                println("Requesting Kakao unlink for ID: $kakaoId")

                val headers = HttpHeaders()
                headers.set("Authorization", "KakaoAK $KAKAO_ADMIN_KEY")
                headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

                val params = LinkedMultiValueMap<String, String>()
                params.add("target_id_type", "user_id")
                params.add("target_id", kakaoId)

                val request = HttpEntity(params, headers)
                try {
                    val response = restTemplate.postForEntity("https://kapi.kakao.com/v1/user/unlink", request, String::class.java)
                    println("Kakao unlink success for user ${member.username}: ${response.body}")
                } catch (e: Exception) {
                    println("Kakao server unlink error (proceeding anyway): ${e.message}")
                }
            }

            // 0-1. 네이버 연동 해제 처리 (액세스 토큰 이용)
            if (member.provider == "naver") {
                val accessToken = servletRequest.getSession(false)?.getAttribute("naver_access_token") as? String
                if (accessToken != null) {
                    println("Requesting Naver unlink for user: ${member.username}")
                    val naverUnlinkUrl = "https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=$NAVER_CLIENT_ID&client_secret=$NAVER_CLIENT_SECRET&access_token=$accessToken&service_provider=NAVER"
                    try {
                        val response = restTemplate.getForEntity(naverUnlinkUrl, String::class.java)
                        println("Naver unlink success for user ${member.username}: ${response.body}")
                    } catch (e: Exception) {
                        println("Naver server unlink error (proceeding anyway): ${e.message}")
                    }
                } else {
                    println("Naver access token not found in session for user: ${member.username}")
                }
            }

            // 1. 회원 관련 기록 삭제 (무결성 제약 조건 해결)
            swimmingRecordRepository.deleteAllByMember(member)
            mulOtRecordRepository.deleteAllByMember(member)
            println("All records deleted for user: ${member.username}")

            // 2. 로그아웃 처리
            val auth = SecurityContextHolder.getContext().authentication
            if (auth != null) {
                SecurityContextLogoutHandler().logout(servletRequest, null, auth)
                println("Logout successful for user: ${member.username}")
            }

            // 3. 회원 데이터 삭제
            memberRepository.delete(member)
            println("Member data deleted successfully: ${member.username}")

            return ResponseEntity.ok(mapOf("message" to "${member.provider} 연동 해제가 완료되었습니다."))
        } catch (e: Exception) {
            println("Error during unlink process for user ${member.username}: ${e.message}")
            e.printStackTrace()
            return ResponseEntity.status(500).body(mapOf("message" to "연동 해제 중 서버 오류 발생: ${e.message}"))
        }
    }

    @PostMapping("/register")
    fun register(@RequestBody memberDto: MemberDto): ResponseEntity<Map<String, String>> {
        if (memberRepository.findByUsername(memberDto.username) != null) return ResponseEntity.badRequest().body(mapOf("message" to "중복 아이디"))
        memberRepository.save(Member(username = memberDto.username, password = passwordEncoder.encode(memberDto.password), nickname = memberDto.nickname))
        return ResponseEntity.ok(mapOf("message" to "가입 완료"))
    }
}

data class MemberDto(val username: String = "", val password: String = "", val nickname: String = "")
data class PhoneLoginRequest(val phoneNumber: String = "", val code: String = "")
data class KakaoLoginRequest(val code: String? = null, val redirectUri: String? = null)
data class NaverLoginRequest(val code: String? = null, val state: String? = null)

data class KakaoUserInfo(val id: Long = 0L, val kakao_account: KakaoAccount? = null)
data class KakaoAccount(val profile: KakaoProfile? = null)
data class KakaoProfile(val nickname: String? = null, val profile_image_url: String? = null)
