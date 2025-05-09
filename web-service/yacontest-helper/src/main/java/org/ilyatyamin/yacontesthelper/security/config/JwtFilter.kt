package org.ilyatyamin.yacontesthelper.security.config

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ilyatyamin.yacontesthelper.security.dao.TokenType
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

        logger.info("1")
        val token = authHeader.substring(BEGIN_BEARER_INDEX)

        logger.info("2")
        jwtTokenService.checkThatTokenExistsAndNotExpired(token, TokenType.AUTH)
        logger.info("3")
        val username = jwtTokenService.extractUsername(token)
        logger.info("4")

        if (username.isNotEmpty() && SecurityContextHolder.getContext().authentication == null) {
            logger.info("5")
            val userDetails = userService.userDetailsService().loadUserByUsername(username)

            logger.info("6")
            val context = SecurityContextHolder.createEmptyContext()
            val authToken = UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.authorities
            )
            logger.info("7")

            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            context.authentication = authToken
            SecurityContextHolder.setContext(context)
            logger.info("8")
        }

        logger.info("9")
        filterChain.doFilter(request, response)
    }
}
