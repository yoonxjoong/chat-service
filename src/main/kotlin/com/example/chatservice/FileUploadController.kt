package com.example.chatservice

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@RestController
@RequestMapping("/api/upload")
class FileUploadController {

    @Value("\${upload.path}")
    private lateinit var uploadPath: String

    @PostMapping("/image")
    fun uploadImage(@RequestParam("file") file: MultipartFile): Map<String, String> {
        if (file.isEmpty) throw RuntimeException("File is empty")

        // 1. 디렉토리 생성
        val directory = File(uploadPath)
        if (!directory.exists()) directory.mkdirs()

        // 2. 고유 파일명 생성
        val fileName = UUID.randomUUID().toString() + "_" + file.originalFilename
        val dest = File(directory.absolutePath + File.separator + fileName)

        // 3. 파일 저장
        file.transferTo(dest)

        // 4. 클라이언트가 접근 가능한 URL 반환
        val fileUrl = "/uploads/" + fileName
        return mapOf("url" to fileUrl)
    }
}
