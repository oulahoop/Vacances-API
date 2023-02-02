package fr.iutna.lpmiar.rest.reperitories

import fr.iutna.lpmiar.rest.models.VacancesScolaire
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VacancesScolaireRepository : CrudRepository<VacancesScolaire, Int>