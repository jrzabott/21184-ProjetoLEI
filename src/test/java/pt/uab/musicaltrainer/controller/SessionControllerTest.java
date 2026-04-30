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
    void shouldStartSession() throws Exception {
        String body = "{\"exerciseType\":\"INTERVAL\",\"difficulty\":1}";

        mockMvc.perform(post("/api/sessions/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.sessionId").isNumber())
            .andExpect(jsonPath("$.startedAt").isString());
    }

    @Test
    void shouldEndSession() throws Exception {
        // Criar sessão
        String startBody = "{\"exerciseType\":\"SCALE\",\"difficulty\":2}";
        String resp = mockMvc.perform(post("/api/sessions/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(startBody))
            .andReturn().getResponse().getContentAsString();

        Long sessionId = mapper.readTree(resp).get("sessionId").asLong();

        // Terminar sessão
        mockMvc.perform(post("/api/sessions/" + sessionId + "/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(sessionId))
            .andExpect(jsonPath("$.accuracy").isNumber())
            .andExpect(jsonPath("$.durationSeconds").isNumber());
    }

    @Test
    void shouldReturn404ForEndingNonExistentSession() throws Exception {
        mockMvc.perform(post("/api/sessions/99999/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isNotFound());
    }
}
