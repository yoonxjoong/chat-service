package com.example.chatservice.config

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
            .csrf { it.disable() } // 테스트 편의를 위해 비활성화
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/chat/**", "/ws-stomp/**").authenticated()
                    .anyRequest().permitAll()
            }
            .formLogin { login ->
                login
                    .defaultSuccessUrl("/test2.html", true)
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutSuccessUrl("/login")
                    .permitAll()
            }
        
        return http.build()
    }
}
