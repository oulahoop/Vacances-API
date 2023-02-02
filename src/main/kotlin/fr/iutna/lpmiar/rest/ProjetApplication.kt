package fr.iutna.lpmiar.rest

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(
    title="Vacances API",
    version = "1.0.0"))
class ProjetApplication

fun main(args: Array<String>) {
    runApplication<ProjetApplication>(*args)
}
