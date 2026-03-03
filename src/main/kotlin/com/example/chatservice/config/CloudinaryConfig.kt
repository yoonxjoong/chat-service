package com.example.chatservice.config

import com.cloudinary.Cloudinary
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CloudinaryConfig(
    @Value("\${cloudinary.cloud-name:dzyu6ckvn}") private val cloudName: String,
    @Value("\${cloudinary.api-key:474289284493111}") private val apiKey: String,
    @Value("\${cloudinary.api-secret:0_j_ExwP0l07DMAGJdvPKyxqt9s}") private val apiSecret: String
) {
    @Bean
    fun cloudinary(): Cloudinary {
        return Cloudinary(
            mapOf(
                "cloud_name" to cloudName,
                "api_key" to apiKey,
                "api_secret" to apiSecret
            )
        )
    }
}
