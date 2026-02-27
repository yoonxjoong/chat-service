package com.example.chatservice.config

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .headers { it.frameOptions { frame -> frame.sameOrigin() } }
            .authorizeHttpRequests { auth ->
                auth
                    // 가입 및 중복체크, 번호로그인, 정적 리소스 허용
                    .requestMatchers("/", "/api/member/register", "/api/member/check-id", "/api/member/login/phone", "/api/user/me", "/static/**", "/favicon.ico").permitAll()
                    // 채팅, 수영 기록 및 민감한 정보 수정은 인증 필요
                    .requestMatchers("/chat/**", "/ws-stomp/**", "/api/user/profile", "/api/user/withdraw", "/api/swimming/**").authenticated()
                    .anyRequest().permitAll()
            }
            .exceptionHandling { handling ->
                handling.authenticationEntryPoint { _, response, _ ->
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.contentType = "application/json"
                    response.writer.write("{\"message\":\"Unauthorized\"}")
                }
            }
            .formLogin { login ->
                login
                    .loginProcessingUrl("/api/member/login")
                    .successHandler { _, response, _ ->
                        response.status = HttpServletResponse.SC_OK
                        response.contentType = "application/json"
                        response.writer.write("{\"message\":\"success\"}")
                    }
                    .failureHandler { _, response, _ ->
                        response.status = HttpServletResponse.SC_UNAUTHORIZED
                        response.contentType = "application/json"
                        response.writer.write("{\"message\":\"fail\"}")
                    }
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutUrl("/api/member/logout")
                    .logoutSuccessHandler { _, response, _ ->
                        response.status = HttpServletResponse.SC_OK
                        response.contentType = "application/json"
                        response.writer.write("{\"message\":\"logout success\"}")
                    }
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            }
            .sessionManagement { session ->
                session
                    .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1)
            }
        
        return http.build()
    }
}
