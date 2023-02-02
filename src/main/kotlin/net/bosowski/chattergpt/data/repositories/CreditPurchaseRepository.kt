package net.bosowski.chattergpt.data.repositories

import net.bosowski.chattergpt.data.models.CreditPurchase
import org.springframework.data.repository.CrudRepository

interface CreditPurchaseRepository: CrudRepository<CreditPurchase, Long> {
    fun findBySessionId(sessionId: String): CreditPurchase?
}