package fr.iutna.lpmiar.rest.services

import fr.iutna.lpmiar.rest.models.Academie
import fr.iutna.lpmiar.rest.reperitories.AcademieRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class AcademieService(@Autowired val db: AcademieRepository) {

    /**
     * Find all academies.
     */
    fun findAcademies(): List<Academie> = db.findAll().toList()

    /**
     * Find an academy by its id.
     */
    fun findAcademieById(libelle: String): List<Academie> = db.findById(formatLibelle(libelle)).toList()

    /**
     * Save an academy by its id.
     */
    fun save(academie: Academie): Academie {
        academie.libelle = formatLibelle(academie.libelle)
        return db.save(academie)
    }

    /**
     * Delete an academy by its id.
     */
    fun delete(academie: Academie) {
        academie.libelle = formatLibelle(academie.libelle)
        db.delete(academie)
    }

    /**
     * Edit an academy by its id.
     */
    fun edit(academie: Academie) {
        academie.libelle = formatLibelle(academie.libelle)
        if(db.existsById(academie.libelle)) {
            db.save(academie)
        }
    }

    /**
     * Format a string in title case for libelle of academie.
     */
    fun formatLibelle(libelle: String) = libelle.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }

    fun <T : Any> Optional<out T>.toList(): List<T> =
        if (isPresent) listOf(get()) else emptyList()
}