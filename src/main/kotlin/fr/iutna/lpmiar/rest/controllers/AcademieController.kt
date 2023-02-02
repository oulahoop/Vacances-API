package fr.iutna.lpmiar.rest.controllers

import fr.iutna.lpmiar.rest.models.Academie
import fr.iutna.lpmiar.rest.services.AcademieService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class AcademieController (val service : AcademieService){

    @GetMapping("/api/v1/academies")
    @Operation(summary = "Récupère toutes les academies au sein de la base de données")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = [Content(mediaType = "application/json",
            schema = Schema(implementation = Academie::class))]
    )
    fun getAll(): List<Academie> = service.findAcademies()

    @GetMapping("/api/v1/academies/{libelle}")
    @Operation(summary = "Récupère une académie par son libellé au sein de la base de données")
    @ApiResponses(
        //OK
        ApiResponse(
        responseCode = "200",
        description = "OK",
        content = [Content(mediaType = "application/json",
            schema = Schema(implementation = Academie::class))]),

        //Not Found
        ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = HashMap::class))]),
    )
    fun getOne(@PathVariable libelle: String): ResponseEntity<Any> {
        val academie =  service.findAcademieById(libelle)
        if(academie.isEmpty()) {
            return ResponseEntity(hashMapOf(Pair("error", "not found")), HttpStatus.NOT_FOUND)
        }
        return ResponseEntity.ok(academie[0])
    }

    @PostMapping("/api/v1/academies")
    @Operation(summary = "Création d'une académie au sein de la base de données")
    @ApiResponses(
        //Created
        ApiResponse(
            responseCode = "201",
            description = "Created",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = Academie::class))]),

        //Bad Request
        ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = HashMap::class))]),
    )
    fun post(@RequestBody academie: Academie): ResponseEntity<Any> {
        if(service.findAcademieById(academie.libelle).isNotEmpty()) {
            return ResponseEntity(hashMapOf(Pair("error", "already exists")), HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(service.save(academie), HttpStatus.CREATED)
    }

    @DeleteMapping("/api/v1/academies/{libelle}")
    @Operation(summary = "Suppression d'une académie par son libellé au sein de la base de données")
    @ApiResponses(
        //Accepted
        ApiResponse(
            responseCode = "202",
            description = "Accepted",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = Academie::class))]),

        //Not Found
        ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = HashMap::class))]),
    )
    fun delete(@PathVariable libelle: String): ResponseEntity<Any> {
        val academie = service.findAcademieById(libelle)
        if(academie.isEmpty()) {
            return ResponseEntity(hashMapOf(Pair("error", "not found")), HttpStatus.NOT_FOUND)
        }
        service.delete(academie[0])
        return ResponseEntity(academie[0], HttpStatus.ACCEPTED)
    }

    @PutMapping("/api/v1/academies/{libelle}")
    @Operation(summary = "Modification d'une académie par son libellé au sein de la base de données")
    @ApiResponses(
        //OK
        ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = Academie::class))]),

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
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = HashMap::class))]),
    )
    fun edit(@PathVariable libelle: String, @RequestBody academie: Academie): ResponseEntity<Any> {
        if(academie.libelle != libelle) {
            return ResponseEntity(hashMapOf(Pair("error", "bad request")), HttpStatus.BAD_REQUEST)
        }

        if (service.findAcademieById(libelle).isEmpty()) {
            return ResponseEntity(hashMapOf(Pair("error", "not found")), HttpStatus.NOT_FOUND)
        }

        service.edit(academie)
        return ResponseEntity.ok(academie)
    }
}