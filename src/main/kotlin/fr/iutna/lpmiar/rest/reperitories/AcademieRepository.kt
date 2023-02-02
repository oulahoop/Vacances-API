package fr.iutna.lpmiar.rest.reperitories

import fr.iutna.lpmiar.rest.models.Academie
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AcademieRepository : CrudRepository<Academie, String>