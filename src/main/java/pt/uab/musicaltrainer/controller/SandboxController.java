package pt.uab.musicaltrainer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uab.musicaltrainer.api.NoteInfoResponse;
import pt.uab.musicaltrainer.domain.Interval;
import pt.uab.musicaltrainer.domain.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller para modo sandbox — informação sobre notas e intervalos
 * sem estar num exercício activo.
 */
@RestController
@RequestMapping("/api/sandbox")
public class SandboxController {

    private static final Logger logger = LoggerFactory.getLogger(SandboxController.class);

    @GetMapping("/note-info")
    public ResponseEntity<?> getNoteInfo(@RequestParam String notes) {
        logger.debug("GET /api/sandbox/note-info: notes={}", notes);

        try {
            String[] parts = notes.split(",");
            List<NoteInfoResponse.NoteInfo> noteInfos = new ArrayList<>();

            for (String part : parts) {
                int midi = Integer.parseInt(part.trim());
                Note note = Note.fromMidi(midi);
                // getDisplayName() retorna "C4" — nome + oitava
                noteInfos.add(new NoteInfoResponse.NoteInfo(midi, note.getDisplayName()));
            }

            NoteInfoResponse.IntervalInfo intervalInfo = null;
            if (noteInfos.size() == 2) {
                Note a = Note.fromMidi(Integer.parseInt(parts[0].trim()));
                Note b = Note.fromMidi(Integer.parseInt(parts[1].trim()));
                Interval interval = Interval.between(a, b);
                intervalInfo = new NoteInfoResponse.IntervalInfo(
                    interval.getSemitones(), interval.getName()
                );
            }

            logger.info("Note info: notes={}", notes);
            return ResponseEntity.ok(new NoteInfoResponse(noteInfos, intervalInfo));

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Formato inválido. Usar: ?notes=60 ou ?notes=60,67");
        } catch (Exception e) {
            logger.error("Erro ao obter note info: notes={}", notes, e);
            return ResponseEntity.internalServerError().body("Erro");
        }
    }
}
