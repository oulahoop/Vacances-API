package fr.iutna.lpmiar.rest.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
@Table(name = "utilisateur")
class Utilisateur(var nom: String,
                  var prenom: String,
                  @Column(unique = true) var email: String,
                  private var password: String,
                  @Enumerated(EnumType.STRING) var roles: Role): UserDetails {

    @Id
    @GeneratedValue
    var id: Int? = null

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(roles.name))
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun toString(): String {
        return "Utilisateur(id=$id, nom=$nom, prenom=$prenom, email=$email, password=$password, roles=$roles)"
    }

}