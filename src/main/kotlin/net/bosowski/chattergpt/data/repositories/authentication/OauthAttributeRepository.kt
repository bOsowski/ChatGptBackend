package net.bosowski.chattergpt.data.repositories.authentication

import net.bosowski.chattergpt.data.models.authentication.OauthAttribute
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OauthAttributeRepository : CrudRepository<OauthAttribute, Long> {
}