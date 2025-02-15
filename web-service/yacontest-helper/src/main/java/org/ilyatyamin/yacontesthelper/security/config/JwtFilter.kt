package org.ilyatyamin.yacontesthelper.security.config

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ilyatyamin.yacontesthelper.security.service.JwtTokenService
import org.ilyatyamin.yacontesthelper.security.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Component
class JwtFilter : OncePerRequestFilter() {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jwtTokenService: JwtTokenService

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private const val BEGIN_BEARER_INDEX = 7
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader(AUTHORIZATION_HEADER)

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(BEGIN_BEARER_INDEX)
        val username = jwtTokenService.extractUsername(token)

        if (username.isNotEmpty() && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userService.userDetailsService().loadUserByUsername(username)

            if (jwtTokenService.isTokenNotExpired(token, userDetails)) {
                val context = SecurityContextHolder.createEmptyContext()
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    "",
                    userDetails.authorities
                )

                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                context.authentication = authToken
                SecurityContextHolder.setContext(context)
            }
        }

        filterChain.doFilter(request, response)
    }
}
