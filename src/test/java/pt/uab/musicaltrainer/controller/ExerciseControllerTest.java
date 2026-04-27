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
        GenerateRequest req = new GenerateRequest("INTERVAL", 1);

        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exerciseId").isNumber())
            .andExpect(jsonPath("$.type").value("INTERVAL"))
            .andExpect(jsonPath("$.notes").isArray())
            .andExpect(jsonPath("$.options").isArray())
            .andExpect(jsonPath("$.description").isString());
    }

    @Test
    void shouldGenerateScaleExercise() throws Exception {
        GenerateRequest req = new GenerateRequest("SCALE", 2);

        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value("SCALE"))
            .andExpect(jsonPath("$.notes").isArray());
    }

    @Test
    void shouldGenerateChordExercise() throws Exception {
        GenerateRequest req = new GenerateRequest("CHORD", 1);

        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value("CHORD"));
    }

    @Test
    void shouldReturn400ForUnknownType() throws Exception {
        String body = "{\"type\":\"UNKNOWN\",\"difficulty\":1}";

        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldEvaluateCorrectAnswerForChord() throws Exception {
        // Gerar um exercício
        GenerateRequest req = new GenerateRequest("CHORD", 1);
        String resp = mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andReturn().getResponse().getContentAsString();

        Long exerciseId = mapper.readTree(resp).get("exerciseId").asLong();

        // Responder com uma resposta qualquer (só verificar que o endpoint funciona)
        String answerBody = "{\"sessionId\":null,\"answer\":\"MAJOR\",\"responseTimeMs\":1000}";
        mockMvc.perform(post("/api/exercises/" + exerciseId + "/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(answerBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correct").isBoolean())
            .andExpect(jsonPath("$.correctAnswer").isString())
            .andExpect(jsonPath("$.explanation").isString());
    }
}
