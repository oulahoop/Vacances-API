package fr.iutna.lpmiar.rest.services

import fr.iutna.lpmiar.rest.models.VacancesScolaire
import fr.iutna.lpmiar.rest.reperitories.VacancesScolaireRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class VacancesScolaireService(@Autowired val db: VacancesScolaireRepository) {

    /**
     * Find all vacances.
     */
    fun findVacances(): List<VacancesScolaire> = db.findAll().toList()

    /**
     * Find a vacances by its id.
     */
    fun findVacancesById(id: Int): List<VacancesScolaire> = db.findById(id).toList()

    /**
     * Save a vacances.
     */
    fun save(vacancesScolaire: VacancesScolaire): VacancesScolaire {
        return db.save(vacancesScolaire)
    }

    /**
     * Delete a vacances.
     */
    fun delete(vacancesScolaire: VacancesScolaire) {
        db.delete(vacancesScolaire)
    }

    /**
     * Edit a vacances.
     */
    fun edit(vacancesScolaire: VacancesScolaire) {
        if(db.existsById(vacancesScolaire.id)) {
            db.save(vacancesScolaire)
        }
    }

    fun <T : Any> Optional<out T>.toList(): List<T> =
        if (isPresent) listOf(get()) else emptyList()
}