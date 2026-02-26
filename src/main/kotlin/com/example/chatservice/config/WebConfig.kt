package com.example.chatservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File

@Configuration
class WebConfig : WebMvcConfigurer {
    @Value("\${upload.path}")
    private lateinit var uploadPath: String

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val path = File(uploadPath).absolutePath
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:\$path/")
    }
}
