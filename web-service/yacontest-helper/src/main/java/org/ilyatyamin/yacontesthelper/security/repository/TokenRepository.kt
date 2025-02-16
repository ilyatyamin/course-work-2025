package org.ilyatyamin.yacontesthelper.security.repository

import org.ilyatyamin.yacontesthelper.security.dao.TokenDao
import org.ilyatyamin.yacontesthelper.security.dao.TokenType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TokenRepository : JpaRepository<TokenDao, Long> {
    fun existsTokenDaoByPayloadAndTokenType(payload: String, tokenType: TokenType): Boolean
    fun findByUserIdAndTokenType(userId: Long, tokenType: TokenType): Optional<TokenDao>
    fun findByPayload(payload: String): Optional<TokenDao>
}