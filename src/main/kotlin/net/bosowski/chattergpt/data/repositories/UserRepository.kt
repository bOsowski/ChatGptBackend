package net.bosowski.chattergpt.data.repositories

import net.bosowski.chattergpt.data.models.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Long> {}