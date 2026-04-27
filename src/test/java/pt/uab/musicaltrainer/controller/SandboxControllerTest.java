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
}
