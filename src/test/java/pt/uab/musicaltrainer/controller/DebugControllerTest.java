package pt.uab.musicaltrainer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "db.type=H2")
class DebugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGenerateOnlyRequestedTypesFromBody() throws Exception {
        // o corpo era ignorado - agora o filtro de tipos deve funcionar
        mockMvc.perform(post("/api/debug/exercises/generate-all")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"types\":[\"INTERVAL\"],\"minDifficulty\":1,\"maxDifficulty\":4}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("INTERVAL"))
            .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.lessThanOrEqualTo(4)));
    }

    @Test
    void shouldReturn400ForUnknownTypeInDifficulty() throws Exception {
        // antes devolvia 200 com "UNKNOWN" como se fosse válido
        mockMvc.perform(get("/api/debug/exercises/difficulty/BANANA"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturnAllTypesWhenBodyIsEmpty() throws Exception {
        // corpo vazio ou sem types = gerar todos os tipos
        mockMvc.perform(post("/api/debug/exercises/generate-all")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void shouldReturnHintInGenerateAll() throws Exception {
        String resp = mockMvc.perform(post("/api/debug/exercises/generate-all")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"types\":[\"INTERVAL\"],\"minDifficulty\":1,\"maxDifficulty\":1}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        com.fasterxml.jackson.databind.JsonNode node =
            new com.fasterxml.jackson.databind.ObjectMapper().readTree(resp);
        assertThat(node.get(0).has("hint")).isTrue();
        assertThat(node.get(0).get("hint").asText()).isNotBlank();
    }
}
