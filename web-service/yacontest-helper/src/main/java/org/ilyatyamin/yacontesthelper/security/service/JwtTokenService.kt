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

    internal fun isTokenRefresh(token: String): Boolean {
        return tokenRepository.existsTokenDaoByPayloadAndTokenType(token, TokenType.REFRESH)
    }

    internal fun isTokenNotExpired(token: String): Boolean {
        val username = extractUsername(token)
        return extractUsername(token) == username &&
                !extractClaim(token) { obj: Claims -> obj.expiration }.before(Date())
    }

    private fun updateOnConflictInsertToDb(
        userDetails: UserDao, token: String,
        expiredAt: Date, tokenType: TokenType
    ) {
        val tokenEntity = tokenRepository.findByUserIdAndTokenType(userDetails.id, tokenType)
        if (tokenEntity.isPresent) {
            val dao = tokenEntity.get()
            dao.payload = token
            dao.expiresAt = expiredAt
            dao.updatedAt = LocalDateTime.now()
        } else {
            tokenRepository.save(TokenDao(userDetails.id, token, tokenType, expiredAt))
        }
    }

    private fun <T> extractClaim(token: String, claimsResolvers: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolvers.apply(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        try {
            return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            throw AuthException(
                HttpStatus.UNAUTHORIZED.value(),
                ExceptionMessages.TOKEN_EXPIRED.message
            )
        }
    }

    private fun signingKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(jwtSecret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}
