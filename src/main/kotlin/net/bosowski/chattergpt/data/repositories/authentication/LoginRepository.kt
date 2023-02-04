package net.bosowski.chattergpt.data.repositories.authentication

import net.bosowski.chattergpt.data.models.authentication.Login
import net.bosowski.chattergpt.data.models.authentication.OauthUser
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LoginRepository : CrudRepository<Login, Long> {
    fun findByOauthUser(oauthUser: OauthUser): List<Login>
}