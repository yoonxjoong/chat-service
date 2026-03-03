package com.example.chatservice.image

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
            // Cloudinary 업로드 옵션 설정: 자동 최적화 및 리사이징 적용
            val options = ObjectUtils.asMap(
                "folder", "chat-service",            // 저장 폴더 지정
                "quality", "auto",                    // 자동 품질 최적화 (용량 절감)
                "fetch_format", "auto",               // 최적의 포맷으로 자동 변환 (WebP 등)
                "transformation", "c_limit,w_1280,h_1280" // 최대 1280px로 리사이징 (비율 유지)
            )

            val uploadResult = cloudinary.uploader().upload(file.bytes, options)
            uploadResult["secure_url"] as String
        } catch (e: Exception) {
            println("Cloudinary Upload Error: ${e.message}")
            e.printStackTrace()
            throw RuntimeException("이미지 업로드 중 오류가 발생했습니다: ${e.message}")
        }
    }
}
