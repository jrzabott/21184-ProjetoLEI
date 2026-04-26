package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.dto.SessionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ExerciseDaoTest {
    @Autowired
    private ExerciseDao exerciseDao;

    @Autowired
    private SessionDao sessionDao;

    private Long sessionId;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        SessionRecord session = new SessionRecord(null, now, null, 0, 0, 0, now);
        SessionRecord saved = sessionDao.save(session);
        sessionId = saved.id();
    }

    @Test
    void testSaveExerciseReturnsIdGreaterThanZero() {
        LocalDateTime now = LocalDateTime.now();
        ExerciseRecord exercise = new ExerciseRecord(null, "INTERVAL", 1, "Identify the interval", "PERFECT_FIFTH", now);

        ExerciseRecord saved = exerciseDao.save(exercise);

        assertThat(saved.id()).isGreaterThan(0);
    }

    @Test
    void testSaveExercisePreservesType() {
        LocalDateTime now = LocalDateTime.now();
        ExerciseRecord exercise = new ExerciseRecord(null, "SCALE", 2, "Identify the scale", "MAJOR", now);

        ExerciseRecord saved = exerciseDao.save(exercise);

        assertThat(saved.type()).isEqualTo("SCALE");
    }

    @Test
    void testSaveExercisePreservesDifficulty() {
        LocalDateTime now = LocalDateTime.now();
        ExerciseRecord exercise = new ExerciseRecord(null, "CHORD", 3, "Identify the chord", "MAJOR", now);

        ExerciseRecord saved = exerciseDao.save(exercise);

        assertThat(saved.difficulty()).isEqualTo(3);
    }

    @Test
    void testSaveExercisePreservesQuestion() {
        LocalDateTime now = LocalDateTime.now();
        String question = "What scale is this?";
        ExerciseRecord exercise = new ExerciseRecord(null, "SCALE", 1, question, "MAJOR", now);

        ExerciseRecord saved = exerciseDao.save(exercise);

        assertThat(saved.question()).isEqualTo(question);
    }

    @Test
    void testSaveExercisePreservesCorrectAnswer() {
        LocalDateTime now = LocalDateTime.now();
        String answer = "MINOR_NATURAL";
        ExerciseRecord exercise = new ExerciseRecord(null, "SCALE", 1, "What scale?", answer, now);

        ExerciseRecord saved = exerciseDao.save(exercise);

        assertThat(saved.correctAnswer()).isEqualTo(answer);
    }

    @Test
    void testFindByIdReturnsCorrectExercise() {
        LocalDateTime now = LocalDateTime.now();
        ExerciseRecord exercise = new ExerciseRecord(null, "INTERVAL", 1, "Identify interval", "MAJOR_THIRD", now);
        ExerciseRecord saved = exerciseDao.save(exercise);

        Optional<ExerciseRecord> found = exerciseDao.findById(saved.id());

        assertThat(found).isPresent();
        assertThat(found.get().type()).isEqualTo("INTERVAL");
    }

    @Test
    void testFindByIdReturnsEmptyForNonExistentId() {
        Optional<ExerciseRecord> found = exerciseDao.findById(99999L);

        assertThat(found).isEmpty();
    }

    @Test
    void testFindByTypeReturnsOnlyExercisesOfGivenType() {
        LocalDateTime now = LocalDateTime.now();
        exerciseDao.save(new ExerciseRecord(null, "INTERVAL", 1, "q1", "a1", now));
        exerciseDao.save(new ExerciseRecord(null, "INTERVAL", 1, "q2", "a2", now));
        exerciseDao.save(new ExerciseRecord(null, "SCALE", 1, "q3", "a3", now));

        List<ExerciseRecord> intervals = exerciseDao.findByType("INTERVAL");

        assertThat(intervals).allMatch(e -> e.type().equals("INTERVAL"));
        assertThat(intervals.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testFindByTypeReturnsEmptyListForNonExistentType() {
        List<ExerciseRecord> result = exerciseDao.findByType("NONEXISTENT");

        assertThat(result).isEmpty();
    }

    @Test
    void testFindAllReturnsAllExercises() {
        LocalDateTime now = LocalDateTime.now();
        exerciseDao.save(new ExerciseRecord(null, "INTERVAL", 1, "q1", "a1", now));
        exerciseDao.save(new ExerciseRecord(null, "SCALE", 1, "q2", "a2", now));
        exerciseDao.save(new ExerciseRecord(null, "CHORD", 1, "q3", "a3", now));

        List<ExerciseRecord> all = exerciseDao.findAll();

        assertThat(all.size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void testFindAllOrderedByCreatedAtDescending() {
        LocalDateTime base = LocalDateTime.now();
        exerciseDao.save(new ExerciseRecord(null, "INTERVAL", 1, "q1", "a1", base));
        Thread.sleep(10);
        exerciseDao.save(new ExerciseRecord(null, "SCALE", 1, "q2", "a2", base));

        List<ExerciseRecord> all = exerciseDao.findAll();

        assertThat(all.get(0).createdAt()).isAfterOrEqualTo(all.get(1).createdAt());
    }
}
