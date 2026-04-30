package pt.uab.musicaltrainer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pt.uab.musicaltrainer.api.GenerateRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "db.type=H2")
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldGenerateIntervalExercise() throws Exception {
        GenerateRequest req = new GenerateRequest("INTERVAL", 1, null);

        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exerciseId").isNumber())
            .andExpect(jsonPath("$.type").value("INTERVAL"))
            .andExpect(jsonPath("$.notes").isArray())
            .andExpect(jsonPath("$.description").isString());
    }

    @Test
    void shouldGenerateScaleExercise() throws Exception {
        GenerateRequest req = new GenerateRequest("SCALE", 1, null);

        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value("SCALE"))
            .andExpect(jsonPath("$.notes").isArray());
    }

    @Test
    void shouldReturn400ForUnknownType() throws Exception {
        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"UNKNOWN\",\"difficulty\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAnswerWithNotesAtFlatEndpoint() throws Exception {
        // ADR-014: POST /api/exercises/answer — exerciseId no corpo, notes[] em vez de string
        GenerateRequest req = new GenerateRequest("CHORD", 1, null);
        String resp = mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andReturn().getResponse().getContentAsString();

        Long exerciseId = mapper.readTree(resp).get("exerciseId").asLong();

        // Responder com notas — backend avalia
        String answerBody = String.format(
            "{\"exerciseId\":%d,\"sessionId\":null,\"notes\":[60,64,67],\"responseTimeMs\":1000}",
            exerciseId);

        mockMvc.perform(post("/api/exercises/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(answerBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correct").isBoolean())
            .andExpect(jsonPath("$.correctAnswer").isString())
            .andExpect(jsonPath("$.explanation").isString());
    }

    @Test
    void shouldNotHavePathVariableEndpoint() throws Exception {
        // ADR-014: endpoint /answer é flat, sem /{exerciseId} no path
        mockMvc.perform(post("/api/exercises/99/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isNotFound());
    }
}
