package fr.iutna.lpmiar.rest.config

import fr.iutna.lpmiar.rest.reperitories.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
class ApplicationConfiguration(val userRepository: UserRepository) {

    /**
     * Configure the authentication provider.
     * @return the AuthenticationProvider
     */
    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username -> userRepository.findUserByEmail(username!!) }
    }

    /**
     * Configure the authentication provider.
     * @return the AuthenticationProvider
     */
    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService())
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    /**
     * Configure the password encoder.
     * @param config the AuthenticationConfiguration
     * @return the PasswordEncoder
     * @throws Exception if an error occurs
     */
    @Bean
    @Throws(Exception::class)
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    /**
     * Configure the password encoder.
     * @return the PasswordEncoder
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
