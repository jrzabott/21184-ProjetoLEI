package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.uab.musicaltrainer.domain.Chord;
import pt.uab.musicaltrainer.domain.ChordType;
import pt.uab.musicaltrainer.domain.Note;
import pt.uab.musicaltrainer.dto.ChordQuestion;

import java.util.ArrayList;
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
 * MVP suporta: MAJOR, MINOR, DIMINISHED, AUGMENTED
 * (valores correspondem exactamente ao enum ChordType)
 */
public class ChordExerciseGenerator implements ExerciseGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ChordExerciseGenerator.class);
    private static final Random random = new Random();
    private static final ObjectMapper mapper = new ObjectMapper();

    // Tipos suportados no MVP — correspondentes ao ChordType enum
    private static final List<String> CHORD_TYPES =
        java.util.Arrays.stream(ChordType.values()).map(Enum::name).collect(Collectors.toList());

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.CHORD;
    }

    @Override
    public GeneratedExercise generate(int difficulty) {
        logger.debug("Gerando acorde: difficulty={}", difficulty);

        // Raiz entre C2 (36) e C5 (72)
        int rootMidi = 36 + random.nextInt(37);
        String chordType = CHORD_TYPES.get(random.nextInt(CHORD_TYPES.size()));

        return buildExercise(rootMidi, chordType, difficulty);
    }

    @Override
    public GeneratedExercise fromStored(String questionJson, String correctAnswer, int difficulty) {
        logger.debug("Reconstruindo acorde de BD: questionJson={}", questionJson);
        try {
            ChordQuestion q = mapper.readValue(questionJson, ChordQuestion.class);
            return buildExercise(q.root(), q.type(), difficulty);
        } catch (Exception e) {
            logger.error("Erro a desserializar ChordQuestion: {}", questionJson, e);
            throw new RuntimeException(e);
        }
    }

    private GeneratedExercise buildExercise(int rootMidi, String chordType, int difficulty) {
        Note root = Note.fromMidi(rootMidi);
        Chord chord = Chord.get(chordType, root);

        int[] notes = chord.getNotes().stream()
            .mapToInt(Note::getMidiNumber)
            .toArray();

        String questionJson = "{\"root\":" + rootMidi + ",\"type\":\"" + chordType + "\"}";
        String description = "Que tipo de acorde tem raiz em " + root.getDisplayName() + "?";

        // 4 tipos de acorde — mostrar todos como opções (shuffled)
        List<String> options = new ArrayList<>(CHORD_TYPES);
        Collections.shuffle(options);

        logger.info("Acorde gerado: root={}({}), type={}, difficulty={}",
            rootMidi, root.getDisplayName(), chordType, difficulty);

        return new GeneratedExercise(
            ExerciseType.CHORD.name(), difficulty, questionJson, chordType,
            description, notes, options
        );
    }
}
