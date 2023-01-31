package net.bosowski.chattergpt.data.repositories

import net.bosowski.chattergpt.data.models.OauthAuthority
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OauthAuthorityRepository : CrudRepository<OauthAuthority, Long> {
}