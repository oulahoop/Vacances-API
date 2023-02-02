package fr.iutna.lpmiar.rest.controllers

import fr.iutna.lpmiar.rest.dtos.AuthenticationRequest
import fr.iutna.lpmiar.rest.dtos.AuthenticationResponse
import fr.iutna.lpmiar.rest.dtos.RegisterRequest
import fr.iutna.lpmiar.rest.services.AuthenticationService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(val authenticationService: AuthenticationService) {

    @PostMapping("/register")
    @Operation(summary = "Enregistre un nouvel utilisateur avec pour rôle USER")
    fun register(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authenticationService.register(request))
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authentifie un utilisateur")
    fun authenticate(
        @RequestBody request: AuthenticationRequest
    ): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authenticationService.authenticate(request))
    }
}