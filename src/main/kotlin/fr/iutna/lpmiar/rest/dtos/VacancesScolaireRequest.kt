package fr.iutna.lpmiar.rest.dtos

/**
 * Class DTO (Data Transfer Object) for the Vancances Scolaires Requests (POST/PUT).
 */
data class VacancesScolaireRequest (
    val id              : Int,
    val description     : String,
    val population      : String,
    val dateDebut       : String,
    val dateFin         : String,
    val academieId      : String,
    val zone            : String,
    val anneeScolaire   : String
)