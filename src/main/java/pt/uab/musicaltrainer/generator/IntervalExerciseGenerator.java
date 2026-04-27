package pt.uab.musicaltrainer.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.uab.musicaltrainer.domain.Interval;
import pt.uab.musicaltrainer.domain.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Gera exercícios de identificação de intervalos musicais.
 * <p>
 * Schema de questionJson (ADR-013): {"notes": [midiA, midiB]}
 * correctAnswer: nome do intervalo, ex: "5a Perfeita"
 * <p>
 * Difficulty afecta o range e o tamanho máximo do intervalo:
 * - 1-3: intervalos até 7 semítons, range C3-C5 (mais reconhecíveis)
 * - 4-7: intervalos até 12 semítons, range C2-C6
 * - 8-10: qualquer intervalo, range completo do piano
 */
public class IntervalExerciseGenerator implements ExerciseGenerator {

    private static final Logger logger = LoggerFactory.getLogger(IntervalExerciseGenerator.class);
    private static final Random random = new Random();

    // Nomes dos intervalos para distratores — mapeiam directamente do IntervalImpl
    static final List<String> ALL_INTERVAL_NAMES = Arrays.asList(
        "Unissono", "2a Menor", "2a Maior", "3a Menor", "3a Maior",
        "4a Perfeita", "4a Aumentada / 5a Diminuta", "5a Perfeita",
        "5a Aumentada / 6a Menor", "6a Maior", "6a Aumentada / 7a Menor",
        "7a Maior", "Oitava Perfeita"
    );

    @Override
    public String getExerciseType() {
        return "INTERVAL";
    }

    @Override
    public GeneratedExercise generate(int difficulty) {
        logger.debug("Gerando intervalo: difficulty={}", difficulty);

        int[] midiRange = midiRangeFor(difficulty);
        int maxSemitones = maxSemitonesFor(difficulty);

        // Garantir que noteB >= noteA + 1 e <= 127
        int noteA = midiRange[0] + random.nextInt(Math.max(1, midiRange[1] - midiRange[0] - maxSemitones));
        int noteB = noteA + 1 + random.nextInt(maxSemitones);
        noteB = Math.min(noteB, 127);

        Note low = Note.fromMidi(noteA);
        Note high = Note.fromMidi(noteB);
        Interval interval = Interval.between(low, high);

        // Normalizar nome do intervalo para remover caracteres que seriam confusos
        String correctAnswer = normalizeIntervalName(interval.getName());
        String questionJson = "{\"notes\":[" + noteA + "," + noteB + "]}";
        String description = "Que intervalo existe entre " + low.getDisplayName() + " e " + high.getDisplayName() + "?";

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

        // Parsear {"notes":[midiA, midiB]}
        int[] notes = parseNotesFromJson(questionJson);
        int noteA = notes[0];
        int noteB = notes[1];

        Note low = Note.fromMidi(noteA);
        Note high = Note.fromMidi(noteB);
        String description = "Que intervalo existe entre " + low.getDisplayName() + " e " + high.getDisplayName() + "?";

        return new GeneratedExercise(
            "INTERVAL", difficulty, questionJson, correctAnswer,
            description, new int[]{noteA, noteB}, buildOptions(correctAnswer)
        );
    }

    private int[] parseNotesFromJson(String json) {
        // Parsear {"notes":[60,67]} sem Jackson (sem depender de @SpringBootTest)
        String inner = json.replaceAll(".*\\[(.+)\\].*", "$1");
        String[] parts = inner.split(",");
        return new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
    }

    private String normalizeIntervalName(String name) {
        // IntervalImpl usa caracteres especiais (ª) que podem causar problemas em JSON
        // Normalizar para versões simples mas mantendo coerência
        return name
            .replace("ª", "a")
            .replace("º", "o");
    }

    private List<String> buildOptions(String correct) {
        // Normalizar todos os nomes da lista também
        List<String> normalizedNames = new ArrayList<>();
        for (String name : ALL_INTERVAL_NAMES) {
            normalizedNames.add(normalizeIntervalName(name));
        }

        List<String> pool = new ArrayList<>(normalizedNames);
        pool.remove(correct);
        Collections.shuffle(pool);

        List<String> options = new ArrayList<>();
        options.add(correct);
        options.addAll(pool.subList(0, Math.min(3, pool.size())));
        Collections.shuffle(options);
        return options;
    }

    private int[] midiRangeFor(int difficulty) {
        if (difficulty <= 3) return new int[]{48, 72};  // C3-C5
        if (difficulty <= 7) return new int[]{36, 84};  // C2-C6
        return new int[]{21, 108};                      // A0-C8
    }

    private int maxSemitonesFor(int difficulty) {
        if (difficulty <= 3) return 7;
        if (difficulty <= 7) return 12;
        return 24;
    }
}
