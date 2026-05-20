package pt.uab.musicaltrainer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.dto.SessionRecord;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "db.type=H2")
class SessionServiceIncrementTest {

    @Autowired
    private DaoFactory daoFactory;
    private SessionService service;

    @BeforeEach
    void setUp() { service = new SessionService(daoFactory); }

    @Test
    void shouldIncrementTotalAndCorrect() throws Exception {
        SessionRecord session = service.startSession();
        service.incrementCounters(session.id(), true);
        SessionRecord updated = daoFactory.createSessionDao().findById(session.id()).orElseThrow();
        assertThat(updated.totalExercises()).isEqualTo(1);
        assertThat(updated.correctAnswers()).isEqualTo(1);
        assertThat(updated.incorrectAnswers()).isEqualTo(0);
    }

    @Test
    void shouldIncrementTotalAndIncorrect() throws Exception {
        SessionRecord session = service.startSession();
        service.incrementCounters(session.id(), false);
        SessionRecord updated = daoFactory.createSessionDao().findById(session.id()).orElseThrow();
        assertThat(updated.totalExercises()).isEqualTo(1);
        assertThat(updated.correctAnswers()).isEqualTo(0);
        assertThat(updated.incorrectAnswers()).isEqualTo(1);
    }

    @Test
    void shouldAccumulateMultipleIncrements() throws Exception {
        SessionRecord session = service.startSession();
        service.incrementCounters(session.id(), true);
        service.incrementCounters(session.id(), true);
        service.incrementCounters(session.id(), false);
        SessionRecord updated = daoFactory.createSessionDao().findById(session.id()).orElseThrow();
        assertThat(updated.totalExercises()).isEqualTo(3);
        assertThat(updated.correctAnswers()).isEqualTo(2);
        assertThat(updated.incorrectAnswers()).isEqualTo(1);
    }

    @Test
    void shouldNotIncrementCountersAfterSessionEnded() throws Exception {
        SessionRecord session = service.startSession();
        service.endSession(session.id());

        // confirmar que esta terminada
        SessionRecord ended = daoFactory.createSessionDao().findById(session.id()).orElseThrow();
        assertThat(ended.endTime()).isNotNull();
        int totalBefore = ended.totalExercises();

        // tentar incrementar numa sessao ja terminada - nao deve alterar nada
        service.incrementCounters(session.id(), true);

        SessionRecord after = daoFactory.createSessionDao().findById(session.id()).orElseThrow();
        assertThat(after.totalExercises())
            .as("incrementCounters nao deve alterar sessao ja terminada")
            .isEqualTo(totalBefore);
    }
}
