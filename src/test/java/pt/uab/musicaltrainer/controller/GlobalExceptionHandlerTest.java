package pt.uab.musicaltrainer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "db.type=H2")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn404WithProblemDetailForNonExistentExercise() throws Exception {
        // exerciseId 99999 não existe — antes retornava 500, agora deve ser 404 com ProblemDetail
        mockMvc.perform(post("/api/exercises/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"exerciseId\":99999,\"sessionId\":0,\"notes\":[60,64,67],\"responseTimeMs\":1000}"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").isString());
    }

    @Test
    void shouldReturn404WithProblemDetailForNonExistentSession() throws Exception {
        // sessão 99999 não existe — antes retornava 404 com body vazio, agora com ProblemDetail
        mockMvc.perform(post("/api/sessions/99999/end")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").isString());
    }

    @Test
    void shouldReturn400WithProblemDetailForInvalidType() throws Exception {
        // tipo UNKNOWN — antes retornava plain text, agora ProblemDetail com status 400
        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"UNKNOWN\",\"difficulty\":1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").isString());
    }

    @Test
    void shouldReturn400WithProblemDetailForMissingBody() throws Exception {
        // corpo em falta em endpoint que precisa de JSON — deve ser 400 com ProblemDetail
        mockMvc.perform(post("/api/exercises/generate")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").isString());
    }
}
