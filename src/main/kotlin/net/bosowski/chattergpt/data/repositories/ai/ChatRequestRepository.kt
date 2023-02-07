package net.bosowski.chattergpt.data.repositories.ai

import net.bosowski.chattergpt.data.models.ai.ChatRequest
import net.bosowski.chattergpt.data.models.authentication.OauthUser
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRequestRepository: CrudRepository<ChatRequest, Long> {
    fun findAllByOauthUser(oauthUser: OauthUser): List<ChatRequest>
}