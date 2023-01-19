package net.bosowski.chattergpt

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {}