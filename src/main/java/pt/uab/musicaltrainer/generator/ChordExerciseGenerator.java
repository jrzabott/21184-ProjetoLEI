package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.uab.musicaltrainer.MusicConstants;
import pt.uab.musicaltrainer.domain.Chord;
import pt.uab.musicaltrainer.domain.ChordType;
import pt.uab.musicaltrainer.domain.DifficultyLevel;
import pt.uab.musicaltrainer.domain.Note;
import pt.uab.musicaltrainer.dto.ChordQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        // CORRECT — uses difficulty to filter available chord types
        List<String> available = ChordType.availableFor(band).stream()
            .map(Enum::name)
            .collect(Collectors.toList());

        int rootMidi = MusicConstants.MIDI_MEDIUM_LOW + random.nextInt(MusicConstants.MIDI_EASY_HIGH - MusicConstants.MIDI_MEDIUM_LOW);
        String chordType = available.get(random.nextInt(available.size()));
        logger.debug("Band para difficulty {}: {}", difficulty, band);
        return buildExercise(rootMidi, chordType, difficulty, available);
    }

    @Override
    public GeneratedExercise fromStored(String questionJson, String correctAnswer, int difficulty) {
        logger.debug("Reconstruindo acorde de BD: questionJson={}", questionJson);
        try {
            ChordQuestion q = mapper.readValue(questionJson, ChordQuestion.class);
            List<String> available = ChordType.availableFor(DifficultyLevel.of(difficulty)).stream()
                .map(Enum::name).collect(Collectors.toList());
            return buildExercise(q.root(), q.type(), difficulty, available);
        } catch (Exception e) {
            logger.error("Erro a desserializar ChordQuestion: {}", questionJson, e);
            throw new RuntimeException(e);
        }
    }

    private GeneratedExercise buildExercise(int rootMidi, String chordType, int difficulty, List<String> options) {
        Note root   = Note.fromMidi(rootMidi);
        Chord chord = Chord.get(chordType, root);

        int[] notes = chord.getNotes().stream().mapToInt(Note::getMidiNumber).toArray();

        String questionJson = "{\"root\":" + rootMidi + ",\"type\":\"" + chordType + "\"}";
        String description  = "Que tipo de acorde tem raiz em " + root.getDisplayName() + "?";

        List<String> shuffled = new ArrayList<>(options);
        Collections.shuffle(shuffled);

        logger.info("Acorde gerado: root={}({}), type={}, difficulty={}",
            rootMidi, root.getDisplayName(), chordType, difficulty);

        return new GeneratedExercise(ExerciseType.CHORD.name(), difficulty, questionJson, chordType,
            description, notes, shuffled);
    }
}
