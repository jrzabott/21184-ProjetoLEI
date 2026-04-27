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
        GenerateRequest req = new GenerateRequest("INTERVAL", 3);
        String json = mapper.writeValueAsString(req);
        assertThat(json).contains("INTERVAL").contains("3");
    }

    @Test
    void shouldDeserializeGenerateRequest() throws Exception {
        String json = "{\"type\":\"SCALE\",\"difficulty\":2}";
        GenerateRequest req = mapper.readValue(json, GenerateRequest.class);
        assertThat(req.type()).isEqualTo("SCALE");
        assertThat(req.difficulty()).isEqualTo(2);
    }

    @Test
    void shouldSerializeGenerateResponse() throws Exception {
        GenerateResponse resp = new GenerateResponse(
            1L, "INTERVAL", 2,
            new int[]{60, 67}, "Que intervalo é este?",
            List.of("5a Perfeita", "4a Perfeita", "3a Maior", "2a Maior")
        );
        String json = mapper.writeValueAsString(resp);
        assertThat(json).contains("exerciseId").contains("60").contains("67");
    }

    @Test
    void shouldDeserializeAnswerRequest() throws Exception {
        String json = "{\"sessionId\":5,\"answer\":\"5a Perfeita\",\"responseTimeMs\":2000}";
        AnswerRequest req = mapper.readValue(json, AnswerRequest.class);
        assertThat(req.answer()).isEqualTo("5a Perfeita");
        assertThat(req.sessionId()).isEqualTo(5L);
    }

    @Test
    void shouldSerializeAnswerResponse() throws Exception {
        AnswerResponse resp = new AnswerResponse(true, "MAJOR", "MAJOR", "Correcto!");
        String json = mapper.writeValueAsString(resp);
        assertThat(json).contains("\"correct\":true").contains("MAJOR");
    }

    @Test
    void shouldRejectGenerateRequestWithInvalidDifficulty() {
        assertThatThrownBy(() -> new GenerateRequest("INTERVAL", 0))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new GenerateRequest("INTERVAL", 11))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
