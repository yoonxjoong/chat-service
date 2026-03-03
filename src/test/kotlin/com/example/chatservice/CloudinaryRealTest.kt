package com.example.chatservice

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.junit.jupiter.api.Test
import java.io.File

class CloudinaryRealTest {

    @Test
    fun testRealUpload() {
        val cloudinary = Cloudinary(mapOf(
            "cloud_name" to "dzyu6ckvn",
            "api_key" to "474289284493111",
            "api_secret" to "0_j_ExwP0l07DMAGJdvPKyxqt9s"
        ))

        try {
            // 빈 파일을 하나 생성해서 업로드 테스트
            val tempFile = File.createTempFile("test_upload", ".png")
            tempFile.writeBytes(ByteArray(100))
            
            val result = cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap())
            println("Upload Success: ${result["secure_url"]}")
            tempFile.delete()
        } catch (e: Exception) {
            println("Real Upload Failed: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
