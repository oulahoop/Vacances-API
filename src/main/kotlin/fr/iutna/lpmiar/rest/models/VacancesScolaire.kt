package fr.iutna.lpmiar.rest.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "vacances_scolaire")
data class VacancesScolaire(
    @Id
    val id              : Int,
    val description     : String = "",
    val population      : String = "",
    @Column(name = "date_debut")
    val dateDebut       : Date,
    @Column(name = "date_fin")
    val dateFin         : Date,
    @ManyToOne
    @JoinColumn(name = "academie_id")
    @JsonIgnoreProperties("vacancesScolaire") //ignore les vacances scolaires de l'académie
    val academie        : Academie,
    val zone            : String,
    val anneeScolaire   : String
)