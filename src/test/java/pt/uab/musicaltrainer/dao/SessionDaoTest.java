package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.SessionRecord;
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
class SessionDaoTest {
    @Autowired
    private SessionDao sessionDao;

    @Test
    void testSaveSessionReturnsIdGreaterThanZero() {
        LocalDateTime now = LocalDateTime.now();
        SessionRecord session = new SessionRecord(null, now, null, 0, 0, 0, now);

        SessionRecord saved = sessionDao.save(session);

        assertThat(saved.id()).isGreaterThan(0);
    }

    @Test
    void testSaveSessionPreservesStartTime() {
        LocalDateTime now = LocalDateTime.now();
        SessionRecord session = new SessionRecord(null, now, null, 0, 0, 0, now);

        SessionRecord saved = sessionDao.save(session);

        assertThat(saved.startTime()).isEqualTo(now);
    }

    @Test
    void testFindByIdReturnsCorrectSession() {
        LocalDateTime now = LocalDateTime.now();
        SessionRecord session = new SessionRecord(null, now, null, 0, 0, 0, now);
        SessionRecord saved = sessionDao.save(session);

        Optional<SessionRecord> found = sessionDao.findById(saved.id());

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(saved.id());
        assertThat(found.get().startTime()).isEqualTo(now);
    }

    @Test
    void testFindByIdReturnsEmptyForNonExistentId() {
        Optional<SessionRecord> found = sessionDao.findById(99999L);

        assertThat(found).isEmpty();
    }

    @Test
    void testUpdateSessionModifiesEndTime() {
        LocalDateTime start = LocalDateTime.now();
        SessionRecord session = new SessionRecord(null, start, null, 0, 0, 0, start);
        SessionRecord saved = sessionDao.save(session);

        LocalDateTime end = start.plusHours(1);
        SessionRecord updated = new SessionRecord(saved.id(), start, end, 5, 4, 1, saved.createdAt());
        SessionRecord result = sessionDao.update(updated);

        assertThat(result.endTime()).isEqualTo(end);
    }

    @Test
    void testUpdateSessionModifiesTotalExercises() {
        LocalDateTime now = LocalDateTime.now();
        SessionRecord session = new SessionRecord(null, now, null, 0, 0, 0, now);
        SessionRecord saved = sessionDao.save(session);

        SessionRecord updated = new SessionRecord(saved.id(), now, null, 10, 0, 0, now);
        SessionRecord result = sessionDao.update(updated);

        assertThat(result.totalExercises()).isEqualTo(10);
    }

    @Test
    void testUpdateSessionModifiesCorrectAnswers() {
        LocalDateTime now = LocalDateTime.now();
        SessionRecord session = new SessionRecord(null, now, null, 0, 0, 0, now);
        SessionRecord saved = sessionDao.save(session);

        SessionRecord updated = new SessionRecord(saved.id(), now, null, 10, 8, 2, now);
        SessionRecord result = sessionDao.update(updated);

        assertThat(result.correctAnswers()).isEqualTo(8);
        assertThat(result.incorrectAnswers()).isEqualTo(2);
    }

    @Test
    void testFindAllReturnsAllSessions() {
        LocalDateTime now = LocalDateTime.now();
        sessionDao.save(new SessionRecord(null, now, null, 0, 0, 0, now));
        sessionDao.save(new SessionRecord(null, now.plusHours(1), null, 0, 0, 0, now));
        sessionDao.save(new SessionRecord(null, now.plusHours(2), null, 0, 0, 0, now));

        List<SessionRecord> all = sessionDao.findAll();

        assertThat(all.size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void testFindAllOrderedByCreatedAtDescending() {
        LocalDateTime base = LocalDateTime.now();
        sessionDao.save(new SessionRecord(null, base, null, 0, 0, 0, base));
        Thread.sleep(10);
        sessionDao.save(new SessionRecord(null, base.plusHours(1), null, 0, 0, 0, base));

        List<SessionRecord> all = sessionDao.findAll();

        assertThat(all.get(0).createdAt()).isAfterOrEqualTo(all.get(1).createdAt());
    }
}
