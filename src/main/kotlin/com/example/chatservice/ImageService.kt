package com.example.chatservice

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class ImageService(private val cloudinary: Cloudinary) {

    fun uploadImage(file: MultipartFile): String {
        if (file.isEmpty) {
            throw IllegalArgumentException("파일이 비어있습니다.")
        }
        
        return try {
            val uploadResult = cloudinary.uploader().upload(file.bytes, ObjectUtils.emptyMap())
            uploadResult["secure_url"] as String
        } catch (e: Exception) {
            println("Cloudinary Upload Error: ${e.message}")
            e.printStackTrace()
            throw RuntimeException("이미지 업로드 중 오류가 발생했습니다: ${e.message}")
        }
    }
}
