package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.uab.musicaltrainer.domain.DifficultyLevel;
import pt.uab.musicaltrainer.domain.Note;
import pt.uab.musicaltrainer.domain.Scale;
import pt.uab.musicaltrainer.domain.ScaleType;
import pt.uab.musicaltrainer.dto.ScaleQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gera exercícios de identificação de escalas musicais.
 * <p>
 * Schema de questionJson (ADR-013): {"root": midiRaiz, "type": "MAJOR"}
 * correctAnswer: tipo da escala como string
 * notesToPlay: as notas da escala em sequência ascendente (raiz a raiz+8va)
 * <p>
 * MVP suporta: MAJOR, MINOR_NATURAL, HARMONIC_MINOR — selecionados dinamicamente
 * via ScaleType.availableFor() a partir de uma lista canonizada sem aliases.
 * Iniciantes recebem raízes nas notas brancas (C3-B4) para sons familiares.
 */
public class ScaleExerciseGenerator implements ExerciseGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ScaleExerciseGenerator.class);
    private static final Random random = new Random();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Tipos canónicos do MVP (sem aliases).
     * Derivados de availableFor(INTERMEDIATE), excluindo aliases.
     */
    private static final List<ScaleType> MVP_TYPES = buildMvpTypes();

    private static List<ScaleType> buildMvpTypes() {
        // Tipos canonicos do MVP, sem aliases — IONIAN/AEOLIAN/etc. são excluídos por serem
        // redundantes. Aqui referenciamos directamente os três tipos pedagógicos do MVP.
        return Arrays.asList(ScaleType.MAJOR, ScaleType.MINOR_NATURAL, ScaleType.HARMONIC_MINOR);
    }

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.SCALE;
    }

    @Override
    public GeneratedExercise generate(int difficulty) {
        logger.debug("Gerando escala: difficulty={}", difficulty);

        DifficultyLevel band = DifficultyLevel.of(difficulty);

        // Tipos disponíveis para o nível pedido — intersecção dos MVP com availableFor()
        Set<ScaleType> bandSet = ScaleType.availableFor(band).stream()
            .collect(Collectors.toSet());
        List<String> available = MVP_TYPES.stream()
            .filter(bandSet::contains)
            .map(Enum::name)
            .collect(Collectors.toList());
        if (available.isEmpty()) available = MVP_TYPES.stream().map(Enum::name).collect(Collectors.toList());

        // Raízes brancas (MIDI % 12 in {0,2,4,5,7,9,11}) para iniciantes
        int rootMidi;
        if (band.ordinal() <= DifficultyLevel.ELEMENTARY.ordinal()) {
            int[] whiteRoots = {48,50,52,53,55,57,59,60,62,64,65,67,69,71};
            rootMidi = whiteRoots[random.nextInt(whiteRoots.length)];
        } else {
            rootMidi = 36 + random.nextInt(37);
        }

        String scaleType = available.get(random.nextInt(available.size()));
        // Opções: todos os tipos MVP (para o utilizador identificar qualquer um deles)
        List<String> allMvp = MVP_TYPES.stream().map(Enum::name).collect(Collectors.toList());
        return buildExercise(rootMidi, scaleType, difficulty, allMvp);
    }

    @Override
    public GeneratedExercise fromStored(String questionJson, String correctAnswer, int difficulty) {
        logger.debug("Reconstruindo escala de BD: questionJson={}", questionJson);
        try {
            ScaleQuestion q = mapper.readValue(questionJson, ScaleQuestion.class);
            List<String> allMvp = MVP_TYPES.stream().map(Enum::name).collect(Collectors.toList());
            return buildExercise(q.root(), q.type(), difficulty, allMvp);
        } catch (Exception e) {
            logger.error("Erro a desserializar ScaleQuestion: {}", questionJson, e);
            throw new RuntimeException(e);
        }
    }

    private GeneratedExercise buildExercise(int rootMidi, String scaleType, int difficulty, List<String> options) {
        Note root   = Note.fromMidi(rootMidi);
        Scale scale = Scale.get(scaleType, root);

        // ADR-014: 8 notas — raiz até raiz uma oitava acima (ex: C4 D E F G A B C5)
        List<Note> scaleNotes = scale.getNotes();
        int[] notes = new int[scaleNotes.size() + 1];
        for (int i = 0; i < scaleNotes.size(); i++) {
            notes[i] = scaleNotes.get(i).getMidiNumber();
        }
        notes[scaleNotes.size()] = rootMidi + 12;

        String questionJson = "{\"root\":" + rootMidi + ",\"type\":\"" + scaleType + "\"}";
        String description  = "Que tipo de escala começa em " + root.getDisplayName() + "?";

        List<String> shuffled = new ArrayList<>(options);
        Collections.shuffle(shuffled);

        logger.info("Escala gerada: root={}({}), type={}, difficulty={}",
            rootMidi, root.getDisplayName(), scaleType, difficulty);

        return new GeneratedExercise(ExerciseType.SCALE.name(), difficulty, questionJson, scaleType,
            description, notes, shuffled);
    }
}
