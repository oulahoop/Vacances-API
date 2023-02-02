package fr.iutna.lpmiar.rest.reperitories

import fr.iutna.lpmiar.rest.models.Utilisateur
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<Utilisateur, Int> {

    /**
     * Find a user by its email.
     */
    fun findUserByEmail(email: String): Utilisateur

}