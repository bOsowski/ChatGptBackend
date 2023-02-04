package net.bosowski.chattergpt.data.repositories.authentication

import net.bosowski.chattergpt.data.models.authentication.OauthAuthority
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OauthAuthorityRepository : CrudRepository<OauthAuthority, Long> {
}