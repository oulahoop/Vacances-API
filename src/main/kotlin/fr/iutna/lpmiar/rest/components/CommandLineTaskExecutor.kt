package fr.iutna.lpmiar.rest.components

import fr.iutna.lpmiar.rest.models.Academie
import fr.iutna.lpmiar.rest.models.Role
import fr.iutna.lpmiar.rest.models.Utilisateur
import fr.iutna.lpmiar.rest.models.VacancesScolaire
import fr.iutna.lpmiar.rest.reperitories.AcademieRepository
import fr.iutna.lpmiar.rest.reperitories.UserRepository
import fr.iutna.lpmiar.rest.reperitories.VacancesScolaireRepository
import fr.iutna.lpmiar.rest.services.TaskService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import kotlin.streams.toList


@Profile("!test")
@ConditionalOnProperty(
    prefix = "command.line.runner",
    value = ["enabled"],
    havingValue = "true",
    matchIfMissing = true)
@Component
class CommandLineTaskExecutor(private val taskService: TaskService,
                              private val academieRepository: AcademieRepository,
                              private val vacancesRepository: VacancesScolaireRepository,
                              private val userRepository: UserRepository,
    ):CommandLineRunner {

    override fun run(vararg args: String?) {
        taskService.execute("Initialize data into the database")
        initializeAcademies()
        initializeVacances()
        initializeUsers()
        taskService.execute("Data initialized in the database")
    }

    /**
     * Initialize academies in the database
     * @return
     * @throws Exception
     * @see Academie
     */
    private fun initializeAcademies() {
        val academiePath = "csv/academies-2020.csv"

        for (csvRecord in readCSV(academiePath)) {
            val coords = csvRecord.get(5).split(',')

            val academie = Academie(
                csvRecord.get(0),
                csvRecord.get(2),
                csvRecord.get(3).toInt(),
                csvRecord.get(4).toInt(),
                coords[0].toDouble(),
                coords[1].toDouble()
            )
            academieRepository.save(academie)
        }    }

    /**
     * Initialize vacances in the database
     * @return
     * @throws Exception
     * @see VacancesScolaire
     */
    private fun initializeVacances() {
        val vacancesPath = "csv/calendrier-scolaire.csv"

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        var compteurVancancesId = 1
        for (csvRecord in readCSV(vacancesPath)) {
            try {
                val academie = academieRepository.findById(csvRecord.get(4)).get()
                val vacancesScolaire = VacancesScolaire(
                    compteurVancancesId,
                    csvRecord.get(0),
                    csvRecord.get(1),
                    dateFormat.parse(csvRecord.get(2)),
                    dateFormat.parse(csvRecord.get(3)),
                    academie,
                    csvRecord.get(5),
                    csvRecord.get(6),
                )

                vacancesRepository.save(vacancesScolaire)
                compteurVancancesId++
            }catch (_: Exception) { }
        }
    }

    /**
     * Initialize users with roles in the database
     * @return
     * @throws Exception
     * @see Utilisateur
     */
    private fun initializeUsers() {
        val list = listOf(
            Utilisateur("caubere", "mael", "mael.caubere@etu.univ-nantes.fr", BCryptPasswordEncoder().encode("mael.caubere"), Role.ADMIN),
            Utilisateur("mandou", "hugo", "hugo.mandou@etu.univ-nantes.fr", BCryptPasswordEncoder().encode("hugo.mandou"), Role.ADMIN),
            Utilisateur("random", "random", "random.random@etu.univ-nantes.fr", BCryptPasswordEncoder().encode("random.random"), Role.USER),
        )
        for (user in list.indices) {
            try {
                if(!userRepository.existsById(user+1)) {
                    userRepository.save(list[user])
                }
            }catch (_: Exception) { }
        }
    }

    /**
     * Read a CSV file
     * @param path
     * @return List<CSVRecord>
     * @throws Exception
     */
    private fun readCSV(path: String): List<CSVRecord> {
        // Creation d'un BufferedReader à partir du Path du fichier csv
        val reader = Files.newBufferedReader(Paths.get(path))
        // Parse le fichier avec la lib CSVParser
        val csvParser = CSVParser(reader, CSVFormat.newFormat(';'))

        return csvParser.records.stream().skip(1).toList()
    }
}