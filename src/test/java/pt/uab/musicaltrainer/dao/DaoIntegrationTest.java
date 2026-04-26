package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.SessionRecord;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.dto.ResultRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DaoIntegrationTest {
    private SessionDao sessionDao;
    private ExerciseDao exerciseDao;
    private ResultDao resultDao;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        sessionDao = new SessionDao(dataSource);
        exerciseDao = new ExerciseDao(dataSource);
        resultDao = new ResultDao(dataSource);
    }

    // SessionDao Tests
    @Test
    void testSessionDao_SaveAndRetrieve() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        SessionRecord session = new SessionRecord(null, now, null, 0, 0, 0, now);

        SessionRecord saved = sessionDao.save(session);

        assertThat(saved.id()).isGreaterThan(0);
        assertThat(saved.startTime()).isEqualTo(now);

        SessionRecord retrieved = sessionDao.findById(saved.id()).orElse(null);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.id()).isEqualTo(saved.id());
    }

    @Test
    void testSessionDao_Update() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        SessionRecord session = new SessionRecord(null, start, null, 0, 0, 0, start);
        SessionRecord saved = sessionDao.save(session);

        LocalDateTime end = start.plusHours(1);
        SessionRecord updated = new SessionRecord(saved.id(), start, end, 5, 4, 1, start);
        sessionDao.update(updated);

        SessionRecord retrieved = sessionDao.findById(saved.id()).orElse(null);
        assertThat(retrieved.endTime()).isEqualTo(end);
        assertThat(retrieved.totalExercises()).isEqualTo(5);
    }

    @Test
    void testSessionDao_FindAll() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        sessionDao.save(new SessionRecord(null, now, null, 0, 0, 0, now));
        sessionDao.save(new SessionRecord(null, now.plusHours(1), null, 0, 0, 0, now));

        List<SessionRecord> all = sessionDao.findAll();

        assertThat(all.size()).isGreaterThanOrEqualTo(2);
    }

    // ExerciseDao Tests
    @Test
    void testExerciseDao_SaveAndRetrieve() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ExerciseRecord exercise = new ExerciseRecord(null, "INTERVAL", 1, "Identify interval", "PERFECT_FIFTH", now);

        ExerciseRecord saved = exerciseDao.save(exercise);

        assertThat(saved.id()).isGreaterThan(0);
        assertThat(saved.type()).isEqualTo("INTERVAL");

        ExerciseRecord retrieved = exerciseDao.findById(saved.id()).orElse(null);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.difficulty()).isEqualTo(1);
    }

    @Test
    void testExerciseDao_FindByType() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        exerciseDao.save(new ExerciseRecord(null, "SCALE", 1, "q1", "a1", now));
        exerciseDao.save(new ExerciseRecord(null, "SCALE", 2, "q2", "a2", now));
        exerciseDao.save(new ExerciseRecord(null, "CHORD", 1, "q3", "a3", now));

        List<ExerciseRecord> scales = exerciseDao.findByType("SCALE");

        assertThat(scales).allMatch(e -> e.type().equals("SCALE"));
        assertThat(scales.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testExerciseDao_FindAll() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        exerciseDao.save(new ExerciseRecord(null, "INTERVAL", 1, "q1", "a1", now));
        exerciseDao.save(new ExerciseRecord(null, "SCALE", 1, "q2", "a2", now));

        List<ExerciseRecord> all = exerciseDao.findAll();

        assertThat(all.size()).isGreaterThanOrEqualTo(2);
    }

    // ResultDao Tests
    @Test
    void testResultDao_SaveAndRetrieve() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        SessionRecord session = sessionDao.save(new SessionRecord(null, now, null, 0, 0, 0, now));
        ExerciseRecord exercise = exerciseDao.save(new ExerciseRecord(null, "INTERVAL", 1, "q", "a", now));

        ResultRecord result = new ResultRecord(null, session.id(), exercise.id(), "a", true, now);
        ResultRecord saved = resultDao.save(result);

        assertThat(saved.id()).isGreaterThan(0);
        assertThat(saved.isCorrect()).isTrue();

        ResultRecord retrieved = resultDao.findById(saved.id()).orElse(null);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.sessionId()).isEqualTo(session.id());
    }

    @Test
    void testResultDao_FindBySessionId() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        SessionRecord session = sessionDao.save(new SessionRecord(null, now, null, 0, 0, 0, now));
        ExerciseRecord ex1 = exerciseDao.save(new ExerciseRecord(null, "INTERVAL", 1, "q1", "a1", now));
        ExerciseRecord ex2 = exerciseDao.save(new ExerciseRecord(null, "SCALE", 1, "q2", "a2", now));

        resultDao.save(new ResultRecord(null, session.id(), ex1.id(), "a1", true, now));
        resultDao.save(new ResultRecord(null, session.id(), ex2.id(), "a2", false, now));

        List<ResultRecord> sessionResults = resultDao.findBySessionId(session.id());

        assertThat(sessionResults.size()).isGreaterThanOrEqualTo(2);
        assertThat(sessionResults).allMatch(r -> r.sessionId().equals(session.id()));
    }

    @Test
    void testResultDao_FindAll() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        SessionRecord session = sessionDao.save(new SessionRecord(null, now, null, 0, 0, 0, now));
        ExerciseRecord exercise = exerciseDao.save(new ExerciseRecord(null, "CHORD", 1, "q", "a", now));

        resultDao.save(new ResultRecord(null, session.id(), exercise.id(), "a", true, now));
        resultDao.save(new ResultRecord(null, session.id(), exercise.id(), "b", false, now));

        List<ResultRecord> all = resultDao.findAll();

        assertThat(all.size()).isGreaterThanOrEqualTo(2);
    }
}
