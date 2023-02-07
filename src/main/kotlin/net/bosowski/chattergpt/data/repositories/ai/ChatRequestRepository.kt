package net.bosowski.chattergpt.data.repositories.ai

import net.bosowski.chattergpt.data.models.ai.ChatRequest
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRequestRepository: CrudRepository<ChatRequest, Long>{
}