package pt.uab.musicaltrainer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DtoSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    @Test
    void shouldSerializeGenerateRequest() throws Exception {
        GenerateRequest req = new GenerateRequest("INTERVAL", 3, null);
        String json = mapper.writeValueAsString(req);
        assertThat(json).contains("INTERVAL").contains("3");
    }

    @Test
    void shouldDeserializeGenerateRequest() throws Exception {
        String json = "{\"type\":\"SCALE\",\"difficulty\":2,\"sessionId\":null}";
        GenerateRequest req = mapper.readValue(json, GenerateRequest.class);
        assertThat(req.type()).isEqualTo("SCALE");
        assertThat(req.difficulty()).isEqualTo(2);
    }

    @Test
    void shouldSerializeGenerateResponse() throws Exception {
        GenerateResponse resp = new GenerateResponse(
            1L, "INTERVAL", 2, 2,
            new int[]{60, 67}, "Reproduz o intervalo entre C4 e G4",
            "5a Perfeita — 7 semítons",
            List.of("5a Perfeita", "4a Perfeita", "3a Maior", "2a Maior")
        );
        String json = mapper.writeValueAsString(resp);
        assertThat(json).contains("exerciseId").contains("60").contains("67");
    }

    @Test
    void shouldDeserializeAnswerRequestWithNotes() throws Exception {
        // ADR-014: AnswerRequest usa notes[] em vez de answer string
        // exerciseId no corpo (nao no path)
        String json = "{\"exerciseId\":42,\"sessionId\":5,\"notes\":[60,62,64,65,67,69,71,72],\"responseTimeMs\":6200}";
        AnswerRequest req = mapper.readValue(json, AnswerRequest.class);
        assertThat(req.exerciseId()).isEqualTo(42L);
        assertThat(req.sessionId()).isEqualTo(5L);
        assertThat(req.notes()).containsExactly(60, 62, 64, 65, 67, 69, 71, 72);
        assertThat(req.responseTimeMs()).isEqualTo(6200L);
    }

    @Test
    void shouldSerializeAnswerResponse() throws Exception {
        AnswerResponse resp = new AnswerResponse(true, "[60,62,64]", "[60,62,64]", "Correcto!");
        String json = mapper.writeValueAsString(resp);
        assertThat(json).contains("\"correct\":true");
    }

    @Test
    void shouldRejectGenerateRequestWithInvalidDifficulty() {
        assertThatThrownBy(() -> new GenerateRequest("INTERVAL", 0, null))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GenerateRequest("INTERVAL", 11, null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
