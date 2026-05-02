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
class SandboxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnNoteInfoForSingleNote() throws Exception {
        mockMvc.perform(get("/api/sandbox/note-info").param("notes", "60"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.notes").isArray())
            .andExpect(jsonPath("$.notes[0].midiNumber").value(60))
            .andExpect(jsonPath("$.notes[0].name").value("C4"));
    }

    @Test
    void shouldReturnIntervalForTwoNotes() throws Exception {
        mockMvc.perform(get("/api/sandbox/note-info").param("notes", "60,67"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.notes").isArray())
            .andExpect(jsonPath("$.interval.semitones").value(7))
            .andExpect(jsonPath("$.interval.name").isString());
    }

    @Test
    void shouldReturn400ForInvalidNotes() throws Exception {
        mockMvc.perform(get("/api/sandbox/note-info").param("notes", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForMidiOutOfRange() throws Exception {
        // valores MIDI têm de estar entre 0 e 127
        mockMvc.perform(get("/api/sandbox/note-info").param("notes", "60,200"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNullIntervalForSingleNote() throws Exception {
        // nota única - sem intervalo a calcular, campo interval é null no JSON
        mockMvc.perform(get("/api/sandbox/note-info").param("notes", "60"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.notes[0].midiNumber").value(60))
            .andExpect(jsonPath("$.interval").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void shouldReturn400WithProblemDetailForInvalidNoteFormat() throws Exception {
        // ADR-016: erros devolvem ProblemDetail, não plain text
        mockMvc.perform(get("/api/sandbox/note-info")
                .param("notes", "abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").isString());
    }

    @Test
    void shouldReturn400WithProblemDetailForNoteOutOfRange() throws Exception {
        mockMvc.perform(get("/api/sandbox/note-info")
                .param("notes", "-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").isString());
    }
}
