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

import static org.assertj.core.api.Assertions.assertThat;
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
        // ADR-014: POST /api/exercises/answer - exerciseId no corpo, notes[] em vez de string
        GenerateRequest req = new GenerateRequest("CHORD", 1, null);
        String resp = mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andReturn().getResponse().getContentAsString();

        Long exerciseId = mapper.readTree(resp).get("exerciseId").asLong();

        // Responder com notas - backend avalia
        String answerBody = String.format(
            "{\"exerciseId\":%d,\"sessionId\":null,\"notes\":[60,64,67],\"responseTimeMs\":1000}",
            exerciseId);

        mockMvc.perform(post("/api/exercises/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(answerBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correct").isBoolean())
            // correctAnswer e agora int[] nao String (bug P29 corrigido em feat/70)
            .andExpect(jsonPath("$.correctAnswer").isArray())
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

    @Test
    void shouldReturn400ForInvalidDifficulty() throws Exception {
        // difficulty=0 está abaixo do mínimo (1-10 obrigatório)
        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"INTERVAL\",\"difficulty\":0}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForDifficultyAboveMax() throws Exception {
        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"INTERVAL\",\"difficulty\":11}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForUnknownExerciseType() throws Exception {
        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"HARMONICA\",\"difficulty\":3}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200ForAnswerInSandboxMode() throws Exception {
        // Gerar exercício primeiro
        String genResp = mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"CHORD\",\"difficulty\":1}"))
            .andReturn().getResponse().getContentAsString();
        Long exerciseId = new com.fasterxml.jackson.databind.ObjectMapper()
            .readTree(genResp).get("exerciseId").asLong();

        // Responder com SESSION_NONE (0) - sandbox, sem persistência
        mockMvc.perform(post("/api/exercises/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"exerciseId\":" + exerciseId + ",\"sessionId\":0,\"notes\":[60,64,67],\"responseTimeMs\":1000}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correct").isBoolean())
            // correctAnswer e agora int[] — sandbox mode incluido (bug P29)
            .andExpect(jsonPath("$.correctAnswer").isArray());
    }

    @Test
    void shouldNotReturnOptionsFieldInGenerateResponse() throws Exception {
        // ADR-014: protocolo baseado em notas MIDI — sem multipla escolha
        // O campo options foi removido por ser um vestigio do design original
        String resp = mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"INTERVAL\",\"difficulty\":1}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        com.fasterxml.jackson.databind.JsonNode node =
            new com.fasterxml.jackson.databind.ObjectMapper().readTree(resp);
        assertThat(node.has("options")).isFalse();
    }

    @Test
    void shouldReturnAnswerWithJsonArrayFormat() throws Exception {
        // correctAnswer e userAnswer devem ser JSON arrays sem espaços: [60,67]
        String genResp = mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"INTERVAL\",\"difficulty\":1}"))
            .andReturn().getResponse().getContentAsString();
        Long exerciseId = new com.fasterxml.jackson.databind.ObjectMapper()
            .readTree(genResp).get("exerciseId").asLong();

        String answerResp = mockMvc.perform(post("/api/exercises/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"exerciseId\":" + exerciseId + ",\"sessionId\":0,\"notes\":[60,67],\"responseTimeMs\":500}"))
            .andReturn().getResponse().getContentAsString();

        // correctAnswer e agora um array JSON nativo int[] (bug P29 corrigido)
        // nao mais uma string contendo JSON — verificar que e um array com numeros MIDI
        com.fasterxml.jackson.databind.JsonNode node =
            new com.fasterxml.jackson.databind.ObjectMapper().readTree(answerResp);
        com.fasterxml.jackson.databind.JsonNode correctAnswer = node.get("correctAnswer");
        assertThat(correctAnswer.isArray()).isTrue();
        assertThat(correctAnswer.get(0).isNumber()).isTrue();
    }
}
