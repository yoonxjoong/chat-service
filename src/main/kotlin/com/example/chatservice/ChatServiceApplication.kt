package com.example.chatservice

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
class ChatServiceApplication {
    @Bean
    fun initData(memberRepository: MemberRepository, passwordEncoder: PasswordEncoder) = CommandLineRunner {
        if (memberRepository.findByUsername("user1") == null) {
            memberRepository.save(Member(
                username = "user1",
                password = passwordEncoder.encode("password"),
                nickname = "테스터1"
            ))
        }
    }
}

fun main(args: Array<String>) {
    runApplication<ChatServiceApplication>(*args)
}
