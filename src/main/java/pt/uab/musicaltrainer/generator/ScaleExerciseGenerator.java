package pt.uab.musicaltrainer.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.uab.musicaltrainer.domain.Note;
import pt.uab.musicaltrainer.domain.Scale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Gera exercícios de identificação de escalas musicais.
 * <p>
 * Schema de questionJson (ADR-013): {"root": midiRaiz, "type": "MAJOR"}
 * correctAnswer: tipo da escala como string
 * notesToPlay: as 7 notas da escala em sequência ascendente
 * <p>
 * MVP suporta: MAJOR, MINOR_NATURAL, HARMONIC_MINOR
 * (valores correspondem exactamente ao enum ScaleType)
 */
public class ScaleExerciseGenerator implements ExerciseGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ScaleExerciseGenerator.class);
    private static final Random random = new Random();

    // Tipos suportados no MVP — correspondentes ao ScaleType enum
    static final List<String> SCALE_TYPES = Arrays.asList("MAJOR", "MINOR_NATURAL", "HARMONIC_MINOR");

    @Override
    public String getExerciseType() {
        return "SCALE";
    }

    @Override
    public GeneratedExercise generate(int difficulty) {
        logger.debug("Gerando escala: difficulty={}", difficulty);

        // Raiz entre C2 (36) e C5 (72) para sons claros e reconhecíveis
        int rootMidi = 36 + random.nextInt(37);
        String scaleType = SCALE_TYPES.get(random.nextInt(SCALE_TYPES.size()));

        return buildExercise(rootMidi, scaleType, difficulty);
    }

    @Override
    public GeneratedExercise fromStored(String questionJson, String correctAnswer, int difficulty) {
        logger.debug("Reconstruindo escala de BD: questionJson={}", questionJson);

        int root = parseIntField(questionJson, "root");
        String type = parseStringField(questionJson, "type");

        return buildExercise(root, type, difficulty);
    }

    private GeneratedExercise buildExercise(int rootMidi, String scaleType, int difficulty) {
        Note root = Note.fromMidi(rootMidi);
        Scale scale = Scale.get(scaleType, root);

        // ADR-014: 8 notas — raiz até raiz uma oitava acima (ex: C4 D E F G A B C5)
        java.util.List<Note> scaleNotes = scale.getNotes();
        int[] notes = new int[scaleNotes.size() + 1];
        for (int i = 0; i < scaleNotes.size(); i++) {
            notes[i] = scaleNotes.get(i).getMidiNumber();
        }
        notes[scaleNotes.size()] = rootMidi + 12;

        String questionJson = "{\"root\":" + rootMidi + ",\"type\":\"" + scaleType + "\"}";
        String description = "Que tipo de escala começa em " + root.getDisplayName() + "?";

        // 3 tipos de escala no MVP — mostrar todos como opções (shuffled)
        List<String> options = new ArrayList<>(SCALE_TYPES);
        Collections.shuffle(options);

        logger.info("Escala gerada: root={}({}), type={}, difficulty={}",
            rootMidi, root.getDisplayName(), scaleType, difficulty);

        return new GeneratedExercise(
            "SCALE", difficulty, questionJson, scaleType,
            description, notes, options
        );
    }

    private int parseIntField(String json, String field) {
        // Parsear {"root":60,"type":"MAJOR"} sem Jackson
        String pattern = "\"" + field + "\":(\\d+)";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        if (m.find()) return Integer.parseInt(m.group(1));
        throw new IllegalArgumentException("Campo '" + field + "' não encontrado em: " + json);
    }

    private String parseStringField(String json, String field) {
        String pattern = "\"" + field + "\":\"([^\"]+)\"";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        if (m.find()) return m.group(1);
        throw new IllegalArgumentException("Campo '" + field + "' não encontrado em: " + json);
    }
}
