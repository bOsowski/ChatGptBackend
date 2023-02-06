package net.bosowski.chattergpt.data.repositories.ai

import net.bosowski.chattergpt.data.models.ai.ModelRequest
import org.springframework.data.repository.CrudRepository

interface ModelRequestRepository: CrudRepository<ModelRequest, Long>{
}