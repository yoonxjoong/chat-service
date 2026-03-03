package com.example.chatservice

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/image")
class ImageController(private val imageService: ImageService) {

    @PostMapping("/upload")
    fun uploadImage(@RequestParam("file") file: MultipartFile): Map<String, String> {
        val imageUrl = imageService.uploadImage(file)
        return mapOf("url" to imageUrl)
    }
}
