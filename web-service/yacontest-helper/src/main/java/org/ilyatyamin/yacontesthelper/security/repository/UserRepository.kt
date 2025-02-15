package org.ilyatyamin.yacontesthelper.security.repository

import org.ilyatyamin.yacontesthelper.security.dao.UserDao
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<UserDao?, Long?> {
    fun findByUsername(username: String): Optional<UserDao>
    fun existsByUsername(username: String?): Boolean
    fun existsByEmail(email: String?): Boolean
}
