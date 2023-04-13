package net.bosowski.chattergpt.data.repositories.credits

import net.bosowski.chattergpt.data.models.credits.CreditPurchase
import org.springframework.data.repository.CrudRepository

interface CreditPurchaseRepository: CrudRepository<CreditPurchase, Long> {
    fun findBySessionId(sessionId: String): CreditPurchase?
}