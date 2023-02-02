package fr.iutna.lpmiar.rest.config

import fr.iutna.lpmiar.rest.models.Role
import fr.iutna.lpmiar.rest.models.Utilisateur
import fr.iutna.lpmiar.rest.services.JwtService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthFilter(val jwtService: JwtService, private val userDetailsService: UserDetailsService): OncePerRequestFilter() {

    /**
     * Filter requests to the API.
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @param filterChain the FilterChain
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader: String? = request.getHeader("Authorization")
            val userEmail: String

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                for (uri in SecurityConfiguration.ACCEPTED_URI) {
                    if (request.requestURI.startsWith(uri)
                        || request.requestURI.matches(uri.replace("**",".*").toRegex())) {
                        filterChain.doFilter(request, response)
                        return
                    }
                }
                throw Exception("Unauthenticated")
            }
            val jwtToken: String = authHeader.substring(7)
            try {
                userEmail = jwtService.extractUsername(jwtToken)
            } catch (_: Exception) {
                throw Exception("Invalid token")
            }
            if (SecurityContextHolder.getContext().authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(userEmail)

                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken

                    if(request.method.equals("DELETE", ignoreCase = true)
                        || request.method.equals("PUT", ignoreCase = true)) {
                        if(userDetails is Utilisateur &&
                            userDetails.roles != Role.ADMIN) {
                            throw Exception("Unauthorized")
                        }
                    }
                }
            }
            filterChain.doFilter(request, response)
        } catch (e: Throwable) {
            SecurityContextHolder.clearContext()
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = "application/json"
            response.writer.println("{\"error\": \"${e.message}\"}")
        }
    }
}