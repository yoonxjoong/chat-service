package com.example.chatservice
import com.example.chatservice.chat.*
import com.example.chatservice.member.*
import com.example.chatservice.record.*
import com.example.chatservice.image.*
import com.cloudinary.Cloudinary
import com.cloudinary.Uploader
import com.cloudinary.utils.ObjectUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.springframework.mock.web.MockMultipartFile
import java.io.IOException

class ImageServiceTest {

    @Mock
    private lateinit var cloudinary: Cloudinary

    @Mock
    private lateinit var uploader: Uploader

    private lateinit var imageService: ImageService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(cloudinary.uploader()).thenReturn(uploader)
        imageService = ImageService(cloudinary)
    }

    @Test
    fun `uploadImage should return secure_url when successful`() {
        // Given
        val fileContent = "test image content".toByteArray()
        val mockFile = MockMultipartFile("file", "test.jpg", "image/jpeg", fileContent)
        val uploadResult = mapOf("secure_url" to "https://cloudinary.com/test.jpg")
        
        `when`(uploader.upload(fileContent, ObjectUtils.emptyMap())).thenReturn(uploadResult)

        // When
        val result = imageService.uploadImage(mockFile)

        // Then
        assertEquals("https://cloudinary.com/test.jpg", result)
    }

    @Test
    fun `uploadImage should throw exception when file is empty`() {
        // Given
        val mockFile = MockMultipartFile("file", "", "image/jpeg", ByteArray(0))

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            imageService.uploadImage(mockFile)
        }
    }

    @Test
    fun `uploadImage should throw runtime exception when upload fails`() {
        // Given
        val fileContent = "test image content".toByteArray()
        val mockFile = MockMultipartFile("file", "test.jpg", "image/jpeg", fileContent)
        
        `when`(uploader.upload(fileContent, ObjectUtils.emptyMap())).thenThrow(IOException("Upload failed"))

        // When & Then
        val exception = assertThrows(RuntimeException::class.java) {
            imageService.uploadImage(mockFile)
        }
        assertEquals("이미지 업로드 중 오류가 발생했습니다: Upload failed", exception.message)
    }
}
