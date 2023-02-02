package fr.iutna.lpmiar.rest.dtos

/**
 * Class DTO (Data Transfer Object) for the authentication request.
 */
class AuthenticationRequest() {
    var email:      String = ""
    var password:   String = ""

    constructor(email: String, password: String) : this() {
        this.email = email
        this.password = password
    }
}
