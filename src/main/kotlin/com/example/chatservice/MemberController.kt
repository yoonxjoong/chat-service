package com.example.chatservice

import org.springframework.http.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.HttpClientErrorException
import org.springframework.util.LinkedMultiValueMap
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/member")
class MemberController(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val restTemplate = RestTemplate()

    @PostMapping("/login/kakao")
    fun loginByKakao(
        @RequestBody request: KakaoLoginRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        val code = request.code ?: return ResponseEntity.badRequest().body(mapOf("message" to "인가 코드가 누락되었습니다."))
        val redirectUri = request.redirectUri ?: "http://localhost:8080/login"

        println("Kakao login request received: code=$code, redirectUri=$redirectUri")

        val tokenUrl = "https://kauth.kakao.com/oauth/token"
        val tokenHeaders = HttpHeaders()
        tokenHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        
        val tokenBody = LinkedMultiValueMap<String, String>()
        tokenBody.add("grant_type", "authorization_code")
        // REST API 키 (JavaScript 키와 동일한 경우도 많으나 확인 필요)
        tokenBody.add("client_id", "10370eb1fddb35728f39be1f5a057cb5") 
        tokenBody.add("redirect_uri", redirectUri)
        tokenBody.add("code", code)

        val tokenRequest = HttpEntity(tokenBody, tokenHeaders)
        
        return try {
            val tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map::class.java)
            val accessToken = (tokenResponse.body as Map<*, *>)["access_token"] as String

            val headers = HttpHeaders()
            headers.setBearerAuth(accessToken)
            val userInfoResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                HttpEntity<String>(headers),
                KakaoUserInfo::class.java
            )

            val userInfo = userInfoResponse.body ?: throw RuntimeException("UserInfo is null")
            val kakaoId = userInfo.id.toString()
            
            var member = memberRepository.findByUsername("kakao_$kakaoId")
            if (member == null) {
                member = Member(
                    username = "kakao_$kakaoId",
                    nickname = userInfo.kakao_account?.profile?.nickname ?: "수영인_$kakaoId",
                    password = passwordEncoder.encode("KAKAO_USER"),
                    profileImageUrl = userInfo.kakao_account?.profile?.profile_image_url
                )
                memberRepository.save(member)
            }

            val userDetails = User.builder()
                .username(member.username)
                .password("")
                .authorities("ROLE_USER")
                .build()

            val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            SecurityContextHolder.getContext().authentication = auth
            servletRequest.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext())

            ResponseEntity.ok(mapOf("message" to "로그인 성공", "nickname" to member.nickname))

        } catch (e: HttpClientErrorException) {
            val errorMsg = e.responseBodyAsString
            println("Kakao API Error: $errorMsg")
            ResponseEntity.status(e.statusCode).body(mapOf("message" to "카카오 오류: $errorMsg"))
        } catch (e: Exception) {
            println("System Error: ${e.message}")
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

    @PostMapping("/register")
    fun register(@RequestBody memberDto: MemberDto): ResponseEntity<Map<String, String>> {
        if (memberRepository.findByUsername(memberDto.username) != null) return ResponseEntity.badRequest().body(mapOf("message" to "중복 아이디"))
        memberRepository.save(Member(username = memberDto.username, password = passwordEncoder.encode(memberDto.password), nickname = memberDto.nickname))
        return ResponseEntity.ok(mapOf("message" to "가입 완료"))
    }
}

data class MemberDto(val username: String = "", val password: String = "", val nickname: String = "")
data class PhoneLoginRequest(val phoneNumber: String = "", val code: String = "")

// JSON 파싱 에러 방지를 위해 기본값을 부여하고 nullable로 설정하거나 기본값 추가
data class KakaoLoginRequest(
    val code: String? = null,
    val redirectUri: String? = null
)

data class KakaoUserInfo(val id: Long = 0L, val kakao_account: KakaoAccount? = null)
data class KakaoAccount(val profile: KakaoProfile? = null)
data class KakaoProfile(val nickname: String? = null, val profile_image_url: String? = null)
