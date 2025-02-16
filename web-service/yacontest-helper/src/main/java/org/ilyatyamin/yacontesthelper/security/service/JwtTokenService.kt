package org.ilyatyamin.yacontesthelper.security.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.ilyatyamin.yacontesthelper.error.AuthException
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.security.dao.TokenDao
import org.ilyatyamin.yacontesthelper.security.dao.TokenType
import org.ilyatyamin.yacontesthelper.security.dao.UserDao
import org.ilyatyamin.yacontesthelper.security.repository.TokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.function.Function
import javax.crypto.SecretKey

@Service
class JwtTokenService(private val tokenRepository: TokenRepository) {
    @Value("\${security.jwt-secret}")
    private val jwtSecret: String? = null

    @Value("\${security.auth-validity-period-seconds}")
    private val authValidityPeriod: Long? = null

    @Value("\${security.refresh-validity-period-seconds}")
    private val refreshValidityPeriod: Long? = null

    internal val tokenType: String = "BEARER"

    internal fun extractUsername(token: String): String {
        return extractClaim(token) { obj: Claims -> obj.subject }
    }

    internal fun generateToken(userDetails: UserDao, tokenType: TokenType): String {
        val time = System.currentTimeMillis()
        val period = if (tokenType == TokenType.AUTH) authValidityPeriod else refreshValidityPeriod
        val expirationDate = Date(time + period!! * 1000)

        val token = Jwts.builder()
            .subject(userDetails.username)
            .issuedAt(Date(time))
            .expiration(expirationDate)
            .signWith(signingKey())
            .compact()

        updateOnConflictInsertToDb(userDetails, token, expirationDate, tokenType)

        return token
    }

    internal fun checkThatTokenExistsAndNotExpired(token: String, tokenType: TokenType) {
        val tokenEntity = tokenRepository.findByPayload(token)
        if (tokenEntity.isEmpty) {
            throw AuthException(
                HttpStatus.UNAUTHORIZED.value(),
                ExceptionMessages.TOKEN_DOES_NOT_EXIST.message
            )
        }
        val tokenDao = tokenEntity.get()
        if (tokenDao.tokenType != tokenType) {
            throw AuthException(
                HttpStatus.UNAUTHORIZED.value(),
                ExceptionMessages.PROVIDED_REFRESH_MUST_AUTH.message
            )
        }

        val username = extractUsername(token)
        val isNotExpired = extractUsername(token) == username &&
                !extractClaim(token) { obj: Claims -> obj.expiration }.before(Date())

        if (!isNotExpired) {
            throw AuthException(
                HttpStatus.UNAUTHORIZED.value(),
                ExceptionMessages.TOKEN_EXPIRED.message
            )
        }
    }

    internal fun isTokenExists(token: String): Boolean {
        return tokenRepository.findByPayload(token).isPresent
    }

    internal fun isTokenRefresh(token: String): Boolean {
        return tokenRepository.existsTokenDaoByPayloadAndTokenType(token, TokenType.REFRESH)
    }

    private fun updateOnConflictInsertToDb(
        userDetails: UserDao, token: String,
        expiredAt: Date, tokenType: TokenType
    ) {
        val tokenEntity = tokenRepository.findByUserIdAndTokenType(userDetails.id, tokenType)
        if (tokenEntity.isPresent) {
            val dao = tokenEntity.get().apply {
                this.payload = token
                this.expiresAt = expiredAt
                this.updatedAt = updatedAt
            }
            tokenRepository.save(dao)
        } else {
            tokenRepository.save(TokenDao(userDetails.id, token, tokenType, expiredAt))
        }
    }

    private fun <T> extractClaim(token: String, claimsResolvers: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolvers.apply(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun signingKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(jwtSecret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}
