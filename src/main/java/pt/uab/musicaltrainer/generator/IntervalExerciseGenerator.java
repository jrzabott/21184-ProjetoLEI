package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.uab.musicaltrainer.MusicConstants;
import pt.uab.musicaltrainer.domain.DifficultyLevel;
import pt.uab.musicaltrainer.domain.IntervalType;
import pt.uab.musicaltrainer.domain.Note;
import pt.uab.musicaltrainer.dto.IntervalQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Gera exercícios de identificação de intervalos musicais.
 * <p>
 * Schema de questionJson (ADR-013): {"notes":[midiA,midiB]}
 * <p>
 * Difficulty afecta o range de raiz e os tipos disponíveis via IntervalType.availableFor().
 * Iniciantes (BEGINNER/ELEMENTARY) ficam em C3-C5; níveis superiores em C2-C6.
 */
public class IntervalExerciseGenerator implements ExerciseGenerator {

    private static final Logger logger = LoggerFactory.getLogger(IntervalExerciseGenerator.class);
    private static final Random random = new Random();

    private final ObjectMapper mapper;

    public IntervalExerciseGenerator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ExerciseType getExerciseType() {
        return ExerciseType.INTERVAL;
    }

    @Override
    public GeneratedExercise generate(int difficulty) {
        logger.debug("Gerando intervalo: difficulty={}", difficulty);

        DifficultyLevel band = DifficultyLevel.of(difficulty);
        List<IntervalType> available = IntervalType.availableFor(band);

        // Raiz mais central para iniciantes
        int[] rootRange = midiRangeFor(difficulty);

        // Exclui Unissono (0 semítons) - noteA == noteB viola o contrato notesToPlay()[1] > [0]
        List<IntervalType> playable = available.stream()
            .filter(t -> t.semitones() > 0)
            .collect(Collectors.toCollection(ArrayList::new));
        if (playable.isEmpty()) playable = available; // fallback de segurança

        int noteA = rootRange[0] + random.nextInt(rootRange[1] - rootRange[0]);
        IntervalType type = playable.get(random.nextInt(playable.size()));
        int noteB = noteA + type.semitones();
        noteB = Math.max(0, Math.min(127, noteB));

        int low  = Math.min(noteA, noteB);
        int high = Math.max(noteA, noteB);

        String correctAnswer = type.internalName();
        String questionJson;
        try {
            questionJson = mapper.writeValueAsString(new IntervalQuestion(new int[]{low, high}));
        } catch (JsonProcessingException e) {
            logger.error("Erro a serializar IntervalQuestion", e);
            throw new RuntimeException(e);
        }
        String description   = "Reproduz o intervalo entre " + Note.fromMidi(low).getDisplayName() + " e " + Note.fromMidi(high).getDisplayName();
        String hint = type.displayName() + " - " + type.semitones()
            + " semítom" + (type.semitones() == 1 ? "" : "s");
        String correctDisplay = type.displayName();

        logger.info("Intervalo gerado: type={}, low={}, high={}, difficulty={}",
            type.internalName(), low, high, difficulty);

        return new GeneratedExercise(
            ExerciseType.INTERVAL.name(), difficulty, questionJson, correctAnswer,
            description, hint, new int[]{low, high}, buildOptions(correctDisplay, available)
        );
    }

    @Override
    public GeneratedExercise fromStored(String questionJson, String correctAnswer, int difficulty) {
        logger.debug("Reconstruindo intervalo de BD: questionJson={}", questionJson);
        try {
            IntervalQuestion q = mapper.readValue(questionJson, IntervalQuestion.class);
            int noteA = q.notes()[0];
            int noteB = q.notes()[1];
            String description = "Reproduz o intervalo entre "
                + Note.fromMidi(noteA).getDisplayName() + " e " + Note.fromMidi(noteB).getDisplayName();
            int semitones = Math.abs(noteB - noteA);
            pt.uab.musicaltrainer.domain.IntervalType intervalType =
                pt.uab.musicaltrainer.domain.IntervalType.fromSemitones(semitones);
            String hint = intervalType.displayName() + " - " + semitones
                + " semítom" + (semitones == 1 ? "" : "s");
            List<String> options = buildOptions(intervalType.displayName(),
                java.util.Arrays.asList(pt.uab.musicaltrainer.domain.IntervalType.values()));
            return new GeneratedExercise(ExerciseType.INTERVAL.name(), difficulty, questionJson,
                correctAnswer, description, hint, new int[]{noteA, noteB}, options);
        } catch (JsonProcessingException e) {
            logger.error("Erro a desserializar IntervalQuestion: {}", questionJson, e);
            throw new RuntimeException(e);
        }
    }

    private int[] midiRangeFor(int difficulty) {
        if (difficulty <= DifficultyLevel.ELEMENTARY.upperBound()) {
            return new int[]{MusicConstants.MIDI_EASY_LOW, MusicConstants.MIDI_EASY_HIGH};
        }
        if (difficulty <= DifficultyLevel.ADVANCED.upperBound()) {
            return new int[]{MusicConstants.MIDI_MEDIUM_LOW, MusicConstants.MIDI_MEDIUM_HIGH};
        }
        return new int[]{21, 108};
    }

    private List<String> buildOptions(String correctDisplay, List<IntervalType> available) {
        List<String> pool = available.stream()
            .map(IntervalType::displayName)
            .filter(n -> !n.equals(correctDisplay))
            .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        java.util.Collections.shuffle(pool);
        List<String> options = new java.util.ArrayList<>();
        options.add(correctDisplay);
        options.addAll(pool.subList(0, Math.min(3, pool.size())));
        java.util.Collections.shuffle(options);
        return options;
    }
}
