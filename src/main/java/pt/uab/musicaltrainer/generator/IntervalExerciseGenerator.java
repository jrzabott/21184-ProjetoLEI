package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.uab.musicaltrainer.domain.Interval;
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
 * Difficulty afecta o range e o tamanho máximo do intervalo:
 * - 1-3: intervalos até 7 semítons, range C3-C5
 * - 4-7: intervalos até 12 semítons, range C2-C6
 * - 8-10: qualquer intervalo, range completo do piano
 */
public class IntervalExerciseGenerator implements ExerciseGenerator {

    private static final Logger logger = LoggerFactory.getLogger(IntervalExerciseGenerator.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    @Override
    public String getExerciseType() {
        return "INTERVAL";
    }

    @Override
    public GeneratedExercise generate(int difficulty) {
        logger.debug("Gerando intervalo: difficulty={}", difficulty);

        int[] midiRange = midiRangeFor(difficulty);
        int maxSemitones = maxSemitonesFor(difficulty);

        int noteA = midiRange[0] + random.nextInt(Math.max(1, midiRange[1] - midiRange[0] - maxSemitones));
        int noteB = noteA + 1 + random.nextInt(maxSemitones);
        noteB = Math.min(noteB, 127);

        Note low = Note.fromMidi(noteA);
        Note high = Note.fromMidi(noteB);
        Interval interval = Interval.between(low, high);

        String correctAnswer = IntervalType.fromSemitones(interval.getSemitones()).displayName();
        String questionJson = "{\"notes\":[" + noteA + "," + noteB + "]}";
        String description = "Que intervalo existe entre " + low.getDisplayName()
            + " e " + high.getDisplayName() + "?";

        logger.info("Intervalo gerado: {}({}) -> {}({}) = {}, difficulty={}",
            noteA, low.getDisplayName(), noteB, high.getDisplayName(), correctAnswer, difficulty);

        return new GeneratedExercise(
            "INTERVAL", difficulty, questionJson, correctAnswer,
            description, new int[]{noteA, noteB}, buildOptions(correctAnswer)
        );
    }

    @Override
    public GeneratedExercise fromStored(String questionJson, String correctAnswer, int difficulty) {
        logger.debug("Reconstruindo intervalo de BD: questionJson={}", questionJson);

        try {
            IntervalQuestion q = mapper.readValue(questionJson, IntervalQuestion.class);
            int noteA = q.notes()[0];
            int noteB = q.notes()[1];
            Note low = Note.fromMidi(noteA);
            Note high = Note.fromMidi(noteB);
            String description = "Que intervalo existe entre " + low.getDisplayName()
                + " e " + high.getDisplayName() + "?";

            return new GeneratedExercise(
                "INTERVAL", difficulty, questionJson, correctAnswer,
                description, new int[]{noteA, noteB}, buildOptions(correctAnswer)
            );
        } catch (Exception e) {
            logger.error("Erro a desserializar IntervalQuestion: {}", questionJson, e);
            throw new RuntimeException(e);
        }
    }

    private List<String> buildOptions(String correct) {
        List<String> pool = Arrays.stream(IntervalType.values())
            .map(IntervalType::displayName)
            .filter(name -> !name.equals(correct))
            .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(pool);

        List<String> options = new ArrayList<>();
        options.add(correct);
        options.addAll(pool.subList(0, Math.min(3, pool.size())));
        Collections.shuffle(options);
        return options;
    }

    private int[] midiRangeFor(int difficulty) {
        if (difficulty <= 3) return new int[]{48, 72};
        if (difficulty <= 7) return new int[]{36, 84};
        return new int[]{21, 108};
    }

    private int maxSemitonesFor(int difficulty) {
        if (difficulty <= 3) return 7;
        if (difficulty <= 7) return 12;
        return 24;
    }
}
