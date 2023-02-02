package fr.iutna.lpmiar.rest.services

import fr.iutna.lpmiar.rest.dtos.AuthenticationRequest
import fr.iutna.lpmiar.rest.dtos.AuthenticationResponse
import fr.iutna.lpmiar.rest.dtos.RegisterRequest
import fr.iutna.lpmiar.rest.models.Role
import fr.iutna.lpmiar.rest.models.Utilisateur
import fr.iutna.lpmiar.rest.reperitories.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val jwtService: JwtService,
    val authenticationManager: AuthenticationManager
) {
    /**
     * Register a new user.
     */
    fun register(request: RegisterRequest): AuthenticationResponse? {
        val utilisateur = Utilisateur(
            request.nom!!,
            request.prenom!!,
            request.email!!,
            passwordEncoder.encode(request.password!!),
            Role.USER
        )
        userRepository.save(utilisateur)
        val jwtToken = jwtService.generateToken(utilisateur)
        return AuthenticationResponse(jwtToken)
    }


    /**
     * Authenticate a user.
     */
    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )
        val utilisateur = userRepository.findUserByEmail(request.email)
        val jwtToken = jwtService.generateToken(utilisateur)
        return AuthenticationResponse(jwtToken)
    }
}