package org.ilyatyamin.yacontesthelper.security.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.ilyatyamin.yacontesthelper.error.AuthException
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function
import javax.crypto.SecretKey

@Service
class JwtTokenService {
    @Value("\${security.jwt-secret}")
    private val jwtSecret: String? = null

    @Value("\${security.validity-period-seconds}")
    private val validityPeriod: Long? = null

    fun extractUsername(token: String): String {
        return extractClaim(token) { obj: Claims -> obj.subject }
    }

    fun generateToken(userDetails: UserDetails): String {
        val time = System.currentTimeMillis()

        return Jwts.builder()
            .subject(userDetails.username)
            .issuedAt(Date(time))
            .expiration(Date(time + validityPeriod!! * 1000))
            .signWith(signingKey())
            .compact()
    }

    fun isTokenNotExpired(token: String, user: UserDetails): Boolean {
        return extractUsername(token) == user.username &&
                !extractClaim(token) { obj: Claims -> obj.expiration }.before(Date())
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
                ExceptionMessages.TOKEN_EXPIRED.message)
        }
    }

    private fun signingKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(jwtSecret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}
