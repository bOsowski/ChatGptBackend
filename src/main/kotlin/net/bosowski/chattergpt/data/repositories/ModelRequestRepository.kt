package net.bosowski.chattergpt.data.repositories

import net.bosowski.chattergpt.data.models.ModelRequest
import org.springframework.data.repository.CrudRepository

interface ModelRequestRepository: CrudRepository<ModelRequest, Long>{
}