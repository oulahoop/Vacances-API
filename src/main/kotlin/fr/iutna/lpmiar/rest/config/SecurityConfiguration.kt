package fr.iutna.lpmiar.rest.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration(val jwtAuthFilter: JwtAuthFilter, val authenticationProvider: AuthenticationProvider) {

    companion object {
        val ACCEPTED_URI = listOf(
            "/api/v1/auth/**",
            "/api/v1/swagger-ui/**",
            "/v3/api-docs",
            "/api/v1/documentation"
        )
    }

    /**
     * Configure the security filter chain.
     * @param http the HttpSecurity to configure
     * @return the SecurityFilterChain
     * @throws Exception if an error occurs
     */
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        for (uri in ACCEPTED_URI) {
            http.authorizeRequests().antMatchers(uri).permitAll()
        }

        http
            .csrf().disable()
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
