package net.bosowski.chattergpt.data.repositories.authentication

import net.bosowski.chattergpt.data.models.authentication.OauthUser
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<OauthUser, Long> {
//    fun findByEmail(email: String): OauthUser?
}