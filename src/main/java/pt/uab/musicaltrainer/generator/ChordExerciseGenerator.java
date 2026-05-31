package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.uab.musicaltrainer.MusicConstants;
import pt.uab.musicaltrainer.domain.Chord;
import pt.uab.musicaltrainer.domain.ChordType;
import pt.uab.musicaltrainer.domain.DifficultyLevel;
import pt.uab.musicaltrainer.domain.IntervalType;
import pt.uab.musicaltrainer.domain.Note;
import pt.uab.musicaltrainer.dto.ChordQuestion;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Gera exercícios de identificação de acordes (tríades).
 * <p>
 * Schema de questionJson (ADR-013): {"root": midiRaiz, "type": "MAJOR"}
 * correctAnswer: tipo do acorde como string
 * notesToPlay: as 3 notas da tríade
 * <p>
 * Tipos disponíveis determinados por ChordType.availableFor() com base na dificuldade.
 */
public class ChordExerciseGenerator implements ExerciseGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ChordExerciseGenerator.class);
    private static final Random random = new Random();
    private static final java.util.Set<Integer> WHITE_KEY_PITCH_CLASSES =
        java.util.Set.of(0, 2, 4, 5, 7, 9, 11);

    private final ObjectMapper mapper;

    public ChordExerciseGenerator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.CHORD;
    }

    @Override
    public GeneratedExercise generate(int difficulty) {
        logger.debug("Gerando acorde: difficulty={}", difficulty);

        DifficultyLevel band = DifficultyLevel.of(difficulty);
        // CORRECT - uses difficulty to filter available chord types
        List<String> available = ChordType.availableFor(band).stream()
            .map(Enum::name)
            .collect(Collectors.toList());

        int rootMidi;
        if (band.ordinal() <= DifficultyLevel.ELEMENTARY.ordinal()) {
            do {
                rootMidi = MusicConstants.MIDI_MEDIUM_LOW
                    + random.nextInt(MusicConstants.MIDI_EASY_HIGH - MusicConstants.MIDI_MEDIUM_LOW);
            } while (!WHITE_KEY_PITCH_CLASSES.contains(rootMidi % 12));
        } else {
            rootMidi = MusicConstants.MIDI_MEDIUM_LOW
                + random.nextInt(MusicConstants.MIDI_EASY_HIGH - MusicConstants.MIDI_MEDIUM_LOW);
        }
        String chordType = available.get(random.nextInt(available.size()));
        logger.debug("Band para difficulty {}: {}", difficulty, band);
        return buildExercise(rootMidi, chordType, difficulty);
    }

    @Override
    public GeneratedExercise fromStored(String questionJson, String correctAnswer, int difficulty) {
        logger.debug("Reconstruindo acorde de BD: questionJson={}", questionJson);
        try {
            ChordQuestion q = mapper.readValue(questionJson, ChordQuestion.class);
            return buildExercise(q.root(), q.type(), difficulty);
        } catch (JsonProcessingException e) {
            logger.error("Erro a desserializar ChordQuestion: {}", questionJson, e);
            throw new RuntimeException(e);
        }
    }

    private GeneratedExercise buildExercise(int rootMidi, String chordType, int difficulty) {
        Note root   = Note.fromMidi(rootMidi);
        Chord chord = Chord.get(chordType, root);

        int[] notes = chord.getNotes().stream().mapToInt(Note::getMidiNumber).toArray();

        String questionJson;
        try {
            questionJson = mapper.writeValueAsString(new ChordQuestion(rootMidi, chordType));
        } catch (JsonProcessingException e) {
            logger.error("Erro a serializar ChordQuestion", e);
            throw new RuntimeException(e);
        }
        String description  = "Toca o acorde " + ChordType.valueOf(chordType).displayName()
            + " com raiz em " + root.getPitchClassName();
        int[] iv = ChordType.valueOf(chordType).getIntervals();
        String hint = "Raiz + " + IntervalType.fromSemitones(iv[1]).displayName()
            + " + " + IntervalType.fromSemitones(iv[2]).displayName();

        logger.info("Acorde gerado: root={}({}), type={}, difficulty={}",
            rootMidi, root.getDisplayName(), chordType, difficulty);

        return new GeneratedExercise(ExerciseType.CHORD.name(), difficulty, questionJson, chordType,
            description, hint, notes);
    }
}
