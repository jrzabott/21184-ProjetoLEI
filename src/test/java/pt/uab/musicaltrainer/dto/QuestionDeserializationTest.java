package pt.uab.musicaltrainer.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class QuestionDeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldDeserializeIntervalQuestion() throws Exception {
        String json = "{\"notes\":[60,67]}";
        IntervalQuestion q = mapper.readValue(json, IntervalQuestion.class);
        assertThat(q.notes()).containsExactly(60, 67);
    }

    @Test
    void shouldDeserializeScaleQuestion() throws Exception {
        String json = "{\"root\":60,\"type\":\"MAJOR\"}";
        ScaleQuestion q = mapper.readValue(json, ScaleQuestion.class);
        assertThat(q.root()).isEqualTo(60);
        assertThat(q.type()).isEqualTo("MAJOR");
    }

    @Test
    void shouldDeserializeChordQuestion() throws Exception {
        String json = "{\"root\":60,\"type\":\"MINOR\"}";
        ChordQuestion q = mapper.readValue(json, ChordQuestion.class);
        assertThat(q.root()).isEqualTo(60);
        assertThat(q.type()).isEqualTo("MINOR");
    }

    @Test
    void shouldDeserializeExpectedNotesArray() throws Exception {
        // correct_answer column stores "[60,62,64]" - Jackson desserializa directamente
        int[] notes = mapper.readValue("[60,62,64]", int[].class);
        assertThat(notes).containsExactly(60, 62, 64);
    }
}
