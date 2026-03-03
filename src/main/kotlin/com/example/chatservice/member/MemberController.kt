package com.example.chatservice.member

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
        tokenBody.add("client_id", "10370eb1fddb35728f39be1f5a057cb5") 
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
        tokenBody.add("client_id", "yE_8k2KwVSQpt144laNn")
        tokenBody.add("client_secret", "LvmhDhk5fj")
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
