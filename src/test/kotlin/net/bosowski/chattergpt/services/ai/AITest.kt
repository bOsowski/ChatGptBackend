package net.bosowski.chattergpt.services.ai

import net.bosowski.chattergpt.controllers.api.AI
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AITest {

    @Autowired
    lateinit var ai: AI

    @Test
    fun getResponse() {
        val response = ai.getResponse()
    }
}