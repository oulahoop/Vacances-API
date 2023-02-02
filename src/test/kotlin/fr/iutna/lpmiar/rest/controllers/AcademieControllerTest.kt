package fr.iutna.lpmiar.rest.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import fr.iutna.lpmiar.rest.dtos.AuthenticationRequest
import fr.iutna.lpmiar.rest.models.Academie
import fr.iutna.lpmiar.rest.models.Role
import fr.iutna.lpmiar.rest.models.Utilisateur
import fr.iutna.lpmiar.rest.reperitories.AcademieRepository
import fr.iutna.lpmiar.rest.reperitories.UserRepository
import fr.iutna.lpmiar.rest.services.AuthenticationService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AcademieControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var academieRepository: AcademieRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    private var token: String = ""

    @BeforeAll
    fun setupToken() {
        userRepository.save(Utilisateur("test", "test", "test@test.com", BCryptPasswordEncoder().encode("test"), Role.ADMIN))
        token = authenticationService.authenticate(
            AuthenticationRequest("test@test.com", "test")
        ).token
    }

    @Test
    @Order(0)
    fun emptyAcademies() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/academies")
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
    fun createAcademie() {
        val academie = Academie("Paris", "Paris", 1, 1, 2.0, 2.0)

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/academies")
                .content(mapper.writeValueAsString(academie))
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.libelle").value("Paris"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.region").value("Paris"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(2.0))
            .andReturn()
    }

    @Test
    @Order(2)
    fun createAcademieWithSameLibelle() {
        val academie = Academie("Paris", "Paris", 1, 1, 2.0, 2.0)

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/academies")
                .content(mapper.writeValueAsString(academie))
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("already exists"))
            .andReturn()
    }

    @Test
    @Order(3)
    fun getAcademies() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/academies")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].libelle").value("Paris"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].region").value("Paris"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].longitude").value(2.0))
            .andReturn()
    }

    @Test
    @Order(4)
    fun editAcademie() {
        val academie = Academie("Paris", "Ile de France", 1, 1, 2.0, 2.0)

        mockMvc.perform(
            MockMvcRequestBuilders
                .put("/api/v1/academies/Paris")
                .header("Authorization", "Bearer $token")
                .content(mapper.writeValueAsString(academie))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.libelle").value("Paris"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.region").value("Ile de France"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(2.0))
            .andReturn()
    }

    @Test
    @Order(5)
    fun deleteAcademie() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/academies/Paris")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isAccepted)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.libelle").value("Paris"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.region").value("Ile de France"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(2.0))
            .andReturn()
    }

    @Test
    @Order(6)
    fun deleteAcademieNotFound() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/academies/paris")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("not found"))
            .andReturn()
    }

    @Test
    @AfterAll
    fun tearDown() {
        academieRepository.deleteAll()
    }

}