package net.bosowski.chattergpt.services.ai

import net.bosowski.chattergpt.controllers.api.AIService
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AIServiceTest {

    @Autowired
    lateinit var aiService: AIService

    @Test
    fun getResponse() {
        val response = aiService.getResponse()
    }
}