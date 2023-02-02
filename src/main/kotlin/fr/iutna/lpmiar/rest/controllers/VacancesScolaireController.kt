package fr.iutna.lpmiar.rest.controllers

import fr.iutna.lpmiar.rest.dtos.VacancesScolaireRequest
import fr.iutna.lpmiar.rest.models.Academie
import fr.iutna.lpmiar.rest.models.VacancesScolaire
import fr.iutna.lpmiar.rest.services.AcademieService
import fr.iutna.lpmiar.rest.services.VacancesScolaireService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*


@RestController
class VacancesScolaireController (val service : VacancesScolaireService, val academieService: AcademieService) {

    @GetMapping("/api/v1/vacances")
    @Operation(summary = "Récupère toutes les vacances scolaires au sein de la base de données")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = [Content(mediaType = "application/json",
            schema = Schema(implementation = VacancesScolaire::class))]
    )
    fun getAll(): List<VacancesScolaire> = service.findVacances()

    @GetMapping("/api/v1/vacances/{id}")
    @Operation(summary = "Récupère les vacances scolaires par ID au sein de la base de données")
    @ApiResponses(
        //OK
        ApiResponse(
        responseCode = "200",
        description = "OK",
        content = [Content(mediaType = "application/json",
            schema = Schema(implementation = VacancesScolaire::class))]),

        //Not Found
        ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = HashMap::class))]),
    )
    fun getOne(@PathVariable id: Int): ResponseEntity<Any> {
        val vacancesScolaires =  service.findVacancesById(id)
        if(vacancesScolaires.isEmpty()) {
            return ResponseEntity(hashMapOf(Pair("error", "not found")), HttpStatus.NOT_FOUND)
        }
        return ResponseEntity.ok(vacancesScolaires[0])
    }

    @PostMapping("/api/v1/vacances")
    @Operation(summary = "Création de vacances scolaires au sein de la base de données")
    @ApiResponses(
        //Created
        ApiResponse(
        responseCode = "201",
        description = "Created",
        content = [Content(mediaType = "application/json",
            schema = Schema(implementation = VacancesScolaire::class))]),

        //Bad Request
        ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = HashMap::class))]),
    )
    fun post(@RequestBody request: VacancesScolaireRequest): ResponseEntity<Any> {
        if(service.findVacancesById(request.id).isNotEmpty()) {
            return ResponseEntity(hashMapOf(Pair("error", "already exists")), HttpStatus.BAD_REQUEST)
        }

        val vacancesScolaire = getVacancesByRequest(request)
        //Si c'est une Pair, c'est que c'est une erreur
        if(vacancesScolaire is Pair<*, *>) {
            return ResponseEntity(hashMapOf(vacancesScolaire), HttpStatus.BAD_REQUEST)
        }

        //Si c'est une VacancesScolaire, c'est une bonne réponse
        if(vacancesScolaire is VacancesScolaire) {
            service.save(vacancesScolaire)
            return ResponseEntity(vacancesScolaire, HttpStatus.CREATED)
        }

        //On ne devrait jamais arriver ici
        return ResponseEntity(hashMapOf(Pair("error", "bad request")), HttpStatus.BAD_REQUEST)
    }

    @PutMapping("/api/v1/vacances/{id}")
    @Operation(summary = "Modification de vacances scolaires par ID au sein de la base de données")
    @ApiResponses(
        //OK
        ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = VacancesScolaire::class))]),
        //Bad Request
        ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = HashMap::class))]),
        //Not Found
        ApiResponse(
            responseCode = "404",
            description = "Not Found",
        )
    )
    fun edit(@PathVariable id: Int, @RequestBody request: VacancesScolaireRequest): ResponseEntity<Any> {
        if(request.id != id) {
            return ResponseEntity(hashMapOf(Pair("error", "bad request")), HttpStatus.BAD_REQUEST)
        }

        if (service.findVacancesById(id).isEmpty()) {
            return ResponseEntity(hashMapOf(Pair("error", "not found")), HttpStatus.NOT_FOUND)
        }

        val vacancesScolaire = getVacancesByRequest(request)
        //Si c'est une Pair, c'est que c'est une erreur
        if(vacancesScolaire is Pair<*, *>) {
            return ResponseEntity(hashMapOf(vacancesScolaire), HttpStatus.BAD_REQUEST)
        }

        //Si c'est une VacancesScolaire, c'est une bonne réponse
        if(vacancesScolaire is VacancesScolaire) {
            service.edit(vacancesScolaire)
            return ResponseEntity.ok(vacancesScolaire)
        }

        //On ne devrait jamais arriver ici
        return ResponseEntity(hashMapOf(Pair("error", "bad request")), HttpStatus.BAD_REQUEST)
    }

    //Ajouter a la doc la nécéssité d'être admin
    @DeleteMapping("/api/v1/vacances/{id}")
    @Operation(summary = "Suppression de vacances scolaires par ID au sein de la base de données. Nécessite d'être admin.")
    @ApiResponses(
        //Accepted
        ApiResponse(
        responseCode = "202",
        description = "Accepted",
        content = [Content(mediaType = "application/json",
            schema = Schema(implementation = VacancesScolaire::class))]),

        //Not Found
        ApiResponse(
            responseCode = "404",
            description = "Not Found",
        )
    )
    fun delete(@PathVariable id: Int): ResponseEntity<Any> {
        val vacancesScolaire = service.findVacancesById(id)
        if(vacancesScolaire.isEmpty()) {
            return ResponseEntity(hashMapOf(Pair("error", "not found")), HttpStatus.NOT_FOUND)
        }
        service.delete(vacancesScolaire[0])
        return ResponseEntity(vacancesScolaire[0], HttpStatus.ACCEPTED)
    }

    /**
     * Récupère une vacances scolaire à partir d'un objet VacancesScolaireRequest
     * @param request VacancesScolaireRequest
     * @return VacancesScolaire ou Pair<String, String> (si erreur)
     */
    private fun getVacancesByRequest(request: VacancesScolaireRequest): Any {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateDebut: Date
        val dateFin: Date
        val academie: Academie

        try {
            dateDebut = dateFormat.parse(request.dateDebut)
            dateFin = dateFormat.parse(request.dateFin)
        }catch (_: Exception) {
            return Pair("error", "invalid date format")
        }

        try {
            academie = academieService.findAcademieById(request.academieId)[0]
        }catch (_: Exception) {
            return Pair("error", "academie doesn't exist")
        }

        return VacancesScolaire(
            id = request.id,
            description = request.description,
            population = request.population,
            dateDebut = dateDebut,
            dateFin = dateFin,
            academie = academie,
            zone = request.zone,
            anneeScolaire = request.anneeScolaire
        )
    }
}