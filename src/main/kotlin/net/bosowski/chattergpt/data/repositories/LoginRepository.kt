package net.bosowski.chattergpt.data.repositories

import net.bosowski.chattergpt.data.models.Login
import net.bosowski.chattergpt.data.models.OauthUser
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LoginRepository : CrudRepository<Login, Long> {
    fun findByOauthUser(oauthUser: OauthUser): List<Login>
}