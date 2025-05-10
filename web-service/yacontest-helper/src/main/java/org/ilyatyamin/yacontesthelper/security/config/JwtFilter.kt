package org.ilyatyamin.yacontesthelper.security.config

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ilyatyamin.yacontesthelper.security.enums.TokenType
import org.ilyatyamin.yacontesthelper.security.service.JwtTokenService
import org.ilyatyamin.yacontesthelper.security.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Component
class JwtFilter(
    private var userService: UserService,
    private var jwtTokenService: JwtTokenService
) : OncePerRequestFilter() {
    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEGIN_BEARER_INDEX = 7
        private val logger = LoggerFactory.getLogger(JwtFilter::class.java)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.info("[AUTH] Got request ${request.method} ${request.requestURI}...")

        val authHeader = request.getHeader(AUTHORIZATION_HEADER)
        if (authHeader == null || authHeader.length <= BEGIN_BEARER_INDEX) {
            logger.info("[AUTH] User do not send auth header or it is too short, ignoring...")
            filterChain.doFilter(request, response)
            return
        }
        val token = authHeader.substring(BEGIN_BEARER_INDEX)

        jwtTokenService.checkThatTokenExistsAndNotExpired(token, TokenType.AUTH)
        val username = jwtTokenService.extractUsername(token)

        if (username.isNotEmpty() && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userService.userDetailsService().loadUserByUsername(username)

            val context = SecurityContextHolder.createEmptyContext()
            val authToken = UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.username,
                userDetails.authorities
            )

            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            context.authentication = authToken
            SecurityContextHolder.setContext(context)
        }

        filterChain.doFilter(request, response)
    }
}
