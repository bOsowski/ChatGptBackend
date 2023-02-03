package net.bosowski.chattergpt.data.repositories

import net.bosowski.chattergpt.data.models.OauthAttribute
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OauthAttributeRepository : CrudRepository<OauthAttribute, Long> {
}