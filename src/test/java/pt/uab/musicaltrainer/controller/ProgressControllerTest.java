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
class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnProgressStructure() throws Exception {
        mockMvc.perform(get("/api/progress"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalSessions").isNumber())
            .andExpect(jsonPath("$.totalExercises").isNumber())
            .andExpect(jsonPath("$.overallAccuracy").isNumber())
            .andExpect(jsonPath("$.recentSessions").isArray());
    }

    @Test
    void shouldAlwaysIncludeAllThreeTypesInByType() throws Exception {
        // byType deve ter INTERVAL, SCALE e CHORD mesmo com BD vazia — antes faltavam tipos sem histórico
        mockMvc.perform(get("/api/progress"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.byType.INTERVAL").exists())
            .andExpect(jsonPath("$.byType.SCALE").exists())
            .andExpect(jsonPath("$.byType.CHORD").exists())
            .andExpect(jsonPath("$.byType.INTERVAL.totalAnswers").isNumber())
            .andExpect(jsonPath("$.byType.INTERVAL.accuracy").isNumber());
    }

    @Test
    void shouldIncludeStartedAtInRecentSessions() throws Exception {
        // criar uma sessão para garantir que recentSessions não está vazia
        mockMvc.perform(post("/api/sessions/start")
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        mockMvc.perform(get("/api/progress"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.recentSessions[0].startedAt").isString());
    }
}
