package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class IntervalExerciseGeneratorTest {

    private final IntervalExerciseGenerator generator = new IntervalExerciseGenerator(new ObjectMapper());

    @Test
    void shouldReturnIntervalAsExerciseType() {
        assertThat(generator.getExerciseType()).isEqualTo(pt.uab.musicaltrainer.generator.ExerciseType.INTERVAL);
    }

    @Test
    void shouldGenerateExerciseWithTwoMidiNotes() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.notesToPlay()).hasSize(2);
        assertThat(ex.notesToPlay()[0]).isBetween(0, 127);
        assertThat(ex.notesToPlay()[1]).isBetween(0, 127);
    }

    @Test
    void shouldGenerateQuestionJsonWithNotesField() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.questionJson()).contains("\"notes\"");
        assertThat(ex.questionJson()).contains(String.valueOf(ex.notesToPlay()[0]));
        assertThat(ex.questionJson()).contains(String.valueOf(ex.notesToPlay()[1]));
    }

    @Test
    void shouldGenerateNonBlankDescription() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.description()).isNotBlank();
    }

    @Test
    void shouldReconstructFromStoredIntervalQuestion() {
        String questionJson = "{\"notes\":[60,67]}";
        String correctAnswer = "5a Perfeita";

        GeneratedExercise ex = generator.fromStored(questionJson, correctAnswer, 3);

        assertThat(ex.notesToPlay()).containsExactly(60, 67);
        assertThat(ex.correctAnswer()).isEqualTo("5a Perfeita");
    }

    @Test
    void shouldReturnExactMidiNotesFromStoredForEvaluation() {
        // ADR-014: intervalos requerem correspondência exacta de notas MIDI (mesma oitava)
        // D4(62)→A4(69) é também uma 5a Perfeita, mas NÃO é a resposta correcta para C4(60)→G4(67)
        String questionJson = "{\"notes\":[60,67]}";
        GeneratedExercise ex = generator.fromStored(questionJson, "5a Perfeita", 1);

        assertThat(ex.notesToPlay()).containsExactly(60, 67);
        assertThat(ex.notesToPlay()).doesNotContain(62, 69);
    }

    @RepeatedTest(20)
    void shouldGenerateNotesInPianoRange() {
        GeneratedExercise ex = generator.generate(5);
        assertThat(ex.notesToPlay()[0]).isBetween(21, 108);
        assertThat(ex.notesToPlay()[1]).isGreaterThan(ex.notesToPlay()[0]);
    }

    @Test
    void shouldOnlyGenerateBeginnerIntervalsAtDifficulty1() {
        // BEGINNER: UNISSONO(0-suprimido), TERCA_MAIOR(4), QUINTA_PERFEITA(7), OITAVA_PERFEITA(12)
        // Semítons permitidos a difficulty=1: 4, 7, 12 (UNISSONO suprimido por playability)
        java.util.Set<Integer> seenSemitones = new java.util.HashSet<>();
        for (int i = 0; i < 100; i++) {
            GeneratedExercise ex = generator.generate(1);
            int[] notes = ex.notesToPlay();
            seenSemitones.add(Math.abs(notes[1] - notes[0]));
        }
        // TRITONO (6), SEGUNDA_MENOR (1), QUINTA_AUM (8), SEXTA_MAIOR (9) NÃO devem aparecer
        assertThat(seenSemitones).doesNotContain(1, 6, 8, 9);
        // Só semítons BEGINNER: 4 (3ª Maior), 7 (5ª Perfeita), 12 (Oitava)
        assertThat(seenSemitones).isSubsetOf(4, 7, 12);
    }

    @Test
    void shouldGenerateTritonoAtAdvancedDifficulty() {
        // TRITONO e SEGUNDA_MENOR são ADVANCED — devem aparecer a difficulty=7
        java.util.Set<Integer> seenSemitones = new java.util.HashSet<>();
        for (int i = 0; i < 200; i++) {
            GeneratedExercise ex = generator.generate(7);
            int[] notes = ex.notesToPlay();
            seenSemitones.add(Math.abs(notes[1] - notes[0]));
        }
        assertThat(seenSemitones).containsAnyOf(6, 1); // TRITONO=6 ou SEGUNDA_MENOR=1
    }

    @Test
    void descriptionShouldShowTypeAndRootNotBothNotes() {
        for (int i = 0; i < 30; i++) {
            GeneratedExercise ex = generator.generate(3);
            String desc = ex.description();
            assertThat(desc).contains("Toca uma");
            assertThat(desc).contains("a partir de");
            assertThat(desc).doesNotContain("entre");
            String rootName = pt.uab.musicaltrainer.domain.Note.fromMidi(ex.notesToPlay()[0]).getName();
            assertThat(desc).contains(rootName);
        }
    }

    @Test
    void fromStoredDescriptionShouldShowTypeAndRoot() {
        String questionJson = "{\"notes\":[60,67]}";
        GeneratedExercise ex = generator.fromStored(questionJson, "PERFECT_FIFTH", 3);
        assertThat(ex.description()).contains("Toca uma");
        assertThat(ex.description()).contains("a partir de");
        assertThat(ex.description()).doesNotContain("entre");
        assertThat(ex.description()).contains("C");
    }
}
