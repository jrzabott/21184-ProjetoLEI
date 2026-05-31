package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.uab.musicaltrainer.MusicConstants;
import pt.uab.musicaltrainer.domain.DifficultyLevel;
import pt.uab.musicaltrainer.domain.Note;
import pt.uab.musicaltrainer.domain.Scale;
import pt.uab.musicaltrainer.domain.ScaleType;
import pt.uab.musicaltrainer.dto.ScaleQuestion;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Gera exercícios de identificação de escalas musicais.
 * <p>
 * Schema de questionJson (ADR-013): {"root": midiRaiz, "type": "MAJOR"}
 * correctAnswer: tipo da escala como string
 * notesToPlay: as notas da escala em sequência ascendente (raiz a raiz+8va)
 * <p>
 * Tipos disponiveis por dificuldade via ScaleType.availableFor(), aliases excluidos.
 * Iniciantes recebem raizes nas notas brancas (C3-B4) para sons familiares.
 */
public class ScaleExerciseGenerator implements ExerciseGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ScaleExerciseGenerator.class);
    private static final Random random = new Random();

    private final ObjectMapper mapper;

    public ScaleExerciseGenerator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private static final int[] WHITE_KEY_ROOTS = {48,50,52,53,55,57,59,60,62,64,65,67,69,71};

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.SCALE;
    }

    @Override
    public GeneratedExercise generate(int difficulty) {
        logger.debug("Gerando escala: difficulty={}", difficulty);

        DifficultyLevel band = DifficultyLevel.of(difficulty);

        // Tipos canonicos disponiveis para esta banda — aliases excluidos (IONIAN=MAJOR, etc.)
        List<String> available = ScaleType.availableFor(band).stream()
            .filter(t -> !t.isAlias())
            .map(Enum::name)
            .collect(Collectors.toList());

        if (available.isEmpty()) {
            available = List.of(ScaleType.MAJOR.name());
            logger.warn("Nenhum tipo disponivel para band={}, usando MAJOR como fallback", band);
        }

        // Raizes brancas para iniciantes
        int rootMidi;
        if (band.ordinal() <= DifficultyLevel.ELEMENTARY.ordinal()) {
            rootMidi = WHITE_KEY_ROOTS[random.nextInt(WHITE_KEY_ROOTS.length)];
        } else {
            rootMidi = MusicConstants.MIDI_MEDIUM_LOW
                + random.nextInt(MusicConstants.MIDI_EASY_HIGH - MusicConstants.MIDI_MEDIUM_LOW);
        }

        String scaleType = available.get(random.nextInt(available.size()));
        return buildExercise(rootMidi, scaleType, difficulty);
    }

    @Override
    public GeneratedExercise fromStored(String questionJson, String correctAnswer, int difficulty) {
        logger.debug("Reconstruindo escala de BD: questionJson={}", questionJson);
        try {
            ScaleQuestion q = mapper.readValue(questionJson, ScaleQuestion.class);
            return buildExercise(q.root(), q.type(), difficulty);
        } catch (JsonProcessingException e) {
            logger.error("Erro a desserializar ScaleQuestion: {}", questionJson, e);
            throw new RuntimeException(e);
        }
    }

    private GeneratedExercise buildExercise(int rootMidi, String scaleType, int difficulty) {
        Note root   = Note.fromMidi(rootMidi);
        Scale scale = Scale.get(scaleType, root);

        // ADR-014: 8 notas - raiz até raiz uma oitava acima (ex: C4 D E F G A B C5)
        List<Note> scaleNotes = scale.getNotes();
        int[] notes = new int[scaleNotes.size() + 1];
        for (int i = 0; i < scaleNotes.size(); i++) {
            notes[i] = scaleNotes.get(i).getMidiNumber();
        }
        notes[scaleNotes.size()] = rootMidi + 12;

        String questionJson;
        try {
            questionJson = mapper.writeValueAsString(new ScaleQuestion(rootMidi, scaleType));
        } catch (JsonProcessingException e) {
            logger.error("Erro a serializar ScaleQuestion", e);
            throw new RuntimeException(e);
        }
        String description  = "Toca a escala " + ScaleType.valueOf(scaleType).displayName()
            + " com tónica em " + root.getPitchClassName() + ", de raiz a raiz";
        String hint = buildScaleHint(scaleType);

        logger.info("Escala gerada: root={}({}), type={}, difficulty={}",
            rootMidi, root.getDisplayName(), scaleType, difficulty);

        return new GeneratedExercise(ExerciseType.SCALE.name(), difficulty, questionJson, scaleType,
            description, hint, notes);
    }

    private String buildScaleHint(String scaleType) {
        int[] intervals = ScaleType.valueOf(scaleType).getIntervals();
        StringBuilder sb = new StringBuilder("Fórmula: ");
        for (int i = 1; i < intervals.length; i++) {
            if (i > 1) sb.append(" - ");
            int step = intervals[i] - intervals[i - 1];
            sb.append(step == 1 ? "S" : step == 2 ? "T" : step + "st");
        }
        int lastStep = 12 - intervals[intervals.length - 1];
        sb.append(" - ").append(lastStep == 1 ? "S" : lastStep == 2 ? "T" : lastStep + "st");
        return sb.toString();
    }
}
