package org.ilyatyamin.yacontesthelper.security.repository

import org.ilyatyamin.yacontesthelper.security.dao.TokenDao
import org.ilyatyamin.yacontesthelper.security.enums.TokenType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TokenRepository : JpaRepository<TokenDao, Long> {
    fun findByUserIdAndTokenType(userId: Long, tokenType: TokenType): Optional<TokenDao>
    fun findByPayload(payload: String): Optional<TokenDao>
}