package fr.iutna.lpmiar.rest.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import fr.iutna.lpmiar.rest.dtos.AuthenticationRequest
import fr.iutna.lpmiar.rest.dtos.VacancesScolaireRequest
import fr.iutna.lpmiar.rest.models.Academie
import fr.iutna.lpmiar.rest.models.Role
import fr.iutna.lpmiar.rest.models.Utilisateur
import fr.iutna.lpmiar.rest.reperitories.AcademieRepository
import fr.iutna.lpmiar.rest.reperitories.UserRepository
import fr.iutna.lpmiar.rest.reperitories.VacancesScolaireRepository
import fr.iutna.lpmiar.rest.services.AuthenticationService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import javax.transaction.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class VacancesScolaireTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var academieRepository: AcademieRepository

    @Autowired
    private lateinit var vacancesScolaireRepository: VacancesScolaireRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    private var token: String = ""

    @BeforeEach
    fun setup() {
        // Insert test data into the database before each test
        val academie1 = Academie("Nantes", "Region 1", 1, 1, 48.8566, 2.3522)
        academieRepository.save(academie1)
    }

    @BeforeAll
    fun setupToken() {
        userRepository.save(Utilisateur("test", "test", "test@test.com", BCryptPasswordEncoder().encode("test"), Role.ADMIN))
        token = authenticationService.authenticate(
            AuthenticationRequest("test@test.com", "test")
        ).token
    }

    @Test
    @Transactional
    @Order(0)
    @WithMockUser(username = "user", password = "password", roles = ["ADMIN"])
    fun emptyVacancesScolaire() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/vacances")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().string("[]"))
            .andReturn()
    }

    @Test
    @Order(1)
    @WithMockUser(username = "user", password = "password", roles = ["ADMIN"])
    fun createVacancesScolaire() {
        val vacancesScolaire = VacancesScolaireRequest(
            1,
            "Vacances de Noël",
            "Toute la France",
            "2020-12-24",
            "2021-01-04",
            "Nantes",
            "Zone A",
            "2020-2021")

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/vacances")
                .header("Authorization", "Bearer $token")
                .content(mapper.writeValueAsString(vacancesScolaire))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Vacances de Noël"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.academie.libelle").value("Nantes"))

        val vacancesScolaire2 = VacancesScolaireRequest(2,
            "Vacances de Pâques",
            "Toute la France",
            "2021-04-03",
            "2021-04-18",
            "Nantes",
            "Zone A",
            "2020-2021")

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/vacances")
                .header("Authorization", "Bearer $token")
                .content(mapper.writeValueAsString(vacancesScolaire2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Vacances de Pâques"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.academie.libelle").value("Nantes"))
    }

    @Test
    @Order(2)
    @WithMockUser(username = "user", password = "password", roles = ["ADMIN"])
    fun getVacancesScolaire() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/vacances")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Vacances de Noël"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Vacances de Pâques"))
    }

    @Test
    @Order(3)
    @WithMockUser(username = "user", password = "password", roles = ["ADMIN"])
    fun isAcademieContainVacances() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/academies/Nantes")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.vacancesScolaire[0].description").value("Vacances de Noël"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.vacancesScolaire[1].description").value("Vacances de Pâques"))
    }

    @Test
    @Order(4)
    @WithMockUser(username = "user", password = "password", roles = ["ADMIN"])
    fun deleteVacancesScolaire() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/vacances/2")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isAccepted)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Vacances de Pâques"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.academie.libelle").value("Nantes"))
    }

    @Test
    @Order(5)
    @WithMockUser(username = "user", password = "password", roles = ["ADMIN"])
    fun getVacancesScolaireAfterDelete() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/vacances")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Vacances de Noël"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].academie.libelle").value("Nantes"))
    }

    @Test
    @Order(6)
    @WithMockUser(username = "user", password = "password", roles = ["ADMIN"])
    fun editVacancesScolaire() {

        val vacancesScolaire = VacancesScolaireRequest(1,
            "Vacances",
            "Toute la France",
            "2021-12-24",
            "2021-01-04",
            "Nantes",
            "Zone A",
            "2020-2021")

        mockMvc.perform(
            MockMvcRequestBuilders
                .put("/api/v1/vacances/1")
                .header("Authorization", "Bearer $token")
                .content(mapper.writeValueAsString(vacancesScolaire))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Vacances"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.academie.libelle").value("Nantes"))
    }

    @Test
    @AfterAll
    fun deleteAll() {
        vacancesScolaireRepository.deleteAll()
        academieRepository.deleteAll()
    }
}