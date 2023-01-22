package net.bosowski.chattergpt.data.repositories

import net.bosowski.chattergpt.data.models.OauthAttribute
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AttributeRepository : CrudRepository<OauthAttribute, Long> {
    fun findAllByUserIdAndActiveTrue(userId: Long): List<OauthAttribute>

}