package net.bosowski.chattergpt.data.repositories

import net.bosowski.chattergpt.data.models.OauthUser
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<OauthUser, Long> {
//    fun findByEmail(email: String): OauthUser?
}