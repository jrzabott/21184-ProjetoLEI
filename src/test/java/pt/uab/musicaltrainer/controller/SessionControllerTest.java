package pt.uab.musicaltrainer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldStartSessionWithoutBody() throws Exception {
        // start não precisa de body - antes dava 400 se omitido
        mockMvc.perform(post("/api/sessions/start")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").isNumber())
            .andExpect(jsonPath("$.startedAt").isString())
            .andExpect(jsonPath("$.totalExercises").value(0))
            .andExpect(jsonPath("$.correctAnswers").value(0))
            .andExpect(jsonPath("$.incorrectAnswers").value(0));
    }

    @Test
    void shouldStartSessionWithEmptyBody() throws Exception {
        // body vazio também deve funcionar
        mockMvc.perform(post("/api/sessions/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").isNumber())
            .andExpect(jsonPath("$.totalExercises").value(0));
    }

    @Test
    void shouldEndSessionWithFullShape() throws Exception {
        // end devolve shape completo incluindo startedAt e endedAt
        String resp = mockMvc.perform(post("/api/sessions/start")
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

        Long sessionId = mapper.readTree(resp).get("sessionId").asLong();

        mockMvc.perform(post("/api/sessions/" + sessionId + "/end")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(sessionId))
            .andExpect(jsonPath("$.startedAt").isString())
            .andExpect(jsonPath("$.endedAt").isString())
            .andExpect(jsonPath("$.accuracy").isNumber())
            .andExpect(jsonPath("$.durationSeconds").isNumber());
    }

    @Test
    void shouldReturn200WhenEndCalledTwice() throws Exception {
        // terminar duas vezes é idempotente - sem erro, sem corromper dados
        String resp = mockMvc.perform(post("/api/sessions/start")
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

        Long sessionId = mapper.readTree(resp).get("sessionId").asLong();

        mockMvc.perform(post("/api/sessions/" + sessionId + "/end")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/sessions/" + sessionId + "/end")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.endedAt").isString());
    }

    @Test
    void shouldReturn404WithBodyForNonExistentSession() throws Exception {
        // 404 agora tem body ProblemDetail - antes era vazio
        mockMvc.perform(post("/api/sessions/99999/end")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").isString());
    }
}
