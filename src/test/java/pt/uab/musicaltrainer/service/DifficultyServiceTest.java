package pt.uab.musicaltrainer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.dto.ResultRecord;
import pt.uab.musicaltrainer.dto.SessionRecord;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "db.type=H2")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DifficultyServiceTest {

    @Autowired
    private DaoFactory daoFactory;
    private DifficultyService service;

    @BeforeEach
    void setUp() { service = new DifficultyService(daoFactory); }

    @Test
    void shouldReturnCurrentDifficultyWhenNoHistory() throws Exception {
        assertThat(service.suggestDifficulty("INTERVAL", 5)).isEqualTo(5);
    }

    @Test
    void shouldClampToMinimumOne() throws Exception {
        assertThat(service.suggestDifficulty("SCALE", 1)).isGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldClampToMaximumTen() throws Exception {
        assertThat(service.suggestDifficulty("CHORD", 10)).isLessThanOrEqualTo(10);
    }

    @Test
    void shouldReturnCurrentDifficultyWhenHistorySizeIsInsufficient() throws Exception {
        // menos de 3 tentativas - sem histórico suficiente, mantém dificuldade actual
        assertThat(service.suggestDifficulty("CHORD", 7)).isEqualTo(7);
    }

    // --- Testes de boundary RF09 (P09, P10, P11) ---

    @Test
    void shouldIncrementWhenAccuracyIsExactly80Percent() throws Exception {
        // 80 correctos + 20 errados = exactamente 80% -> incrementa
        SessionRecord session = daoFactory.createSessionDao()
            .save(new SessionRecord(null, LocalDateTime.now(), null, 0, 0, 0, null));
        ExerciseRecord ex = daoFactory.createExerciseDao()
            .save(new ExerciseRecord(null, "INTERVAL", 1, "{\"notes\":[60,67]}", "[60,67]", null));

        for (int i = 0; i < 80; i++) {
            daoFactory.createResultDao().save(
                new ResultRecord(null, session.id(), ex.id(), "[60,67]", true, null));
        }
        for (int i = 0; i < 20; i++) {
            daoFactory.createResultDao().save(
                new ResultRecord(null, session.id(), ex.id(), "[0,1]", false, null));
        }

        int suggested = service.suggestDifficulty("INTERVAL", 5);
        assertThat(suggested).isEqualTo(6); // 5 + 1
    }

    @Test
    void shouldMaintainWhenAccuracyIsExactly40Percent() throws Exception {
        // 40 correctos + 60 errados = exactamente 40% -> mantém (< 0.40 é strict less-than)
        SessionRecord session = daoFactory.createSessionDao()
            .save(new SessionRecord(null, LocalDateTime.now(), null, 0, 0, 0, null));
        ExerciseRecord ex = daoFactory.createExerciseDao()
            .save(new ExerciseRecord(null, "SCALE", 1, "{\"root\":60,\"type\":\"MAJOR\"}", "[60,62,64,65,67,69,71,72]", null));

        for (int i = 0; i < 40; i++) {
            daoFactory.createResultDao().save(
                new ResultRecord(null, session.id(), ex.id(), "[60,62,64,65,67,69,71,72]", true, null));
        }
        for (int i = 0; i < 60; i++) {
            daoFactory.createResultDao().save(
                new ResultRecord(null, session.id(), ex.id(), "[0,1,2,3,4,5,6,7]", false, null));
        }

        int suggested = service.suggestDifficulty("SCALE", 5);
        assertThat(suggested).isEqualTo(5); // mantém - 40% é exactamente o limiar, nao decrementa
    }

    @Test
    void shouldDecrementWhenAccuracyIsBelow40Percent() throws Exception {
        // 39 correctos + 61 errados = 39% -> decrementa
        SessionRecord session = daoFactory.createSessionDao()
            .save(new SessionRecord(null, LocalDateTime.now(), null, 0, 0, 0, null));
        ExerciseRecord ex = daoFactory.createExerciseDao()
            .save(new ExerciseRecord(null, "CHORD", 1, "{\"root\":60,\"type\":\"MAJOR\"}", "[60,64,67]", null));

        for (int i = 0; i < 39; i++) {
            daoFactory.createResultDao().save(
                new ResultRecord(null, session.id(), ex.id(), "[60,64,67]", true, null));
        }
        for (int i = 0; i < 61; i++) {
            daoFactory.createResultDao().save(
                new ResultRecord(null, session.id(), ex.id(), "[0,1,2]", false, null));
        }

        int suggested = service.suggestDifficulty("CHORD", 5);
        assertThat(suggested).isEqualTo(4); // 5 - 1
    }
}
