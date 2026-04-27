package pt.uab.musicaltrainer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
}
