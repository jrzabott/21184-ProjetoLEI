package pt.uab.musicaltrainer.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.dto.ResultRecord;
import pt.uab.musicaltrainer.dto.SessionRecord;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "db.type=H2")
class ResultDaoNoRepeatTest {

    @Autowired
    private DaoFactory daoFactory;

    @Test
    void shouldReturnEmptyWhenSessionHasNoResults() throws Exception {
        SessionRecord session = daoFactory.createSessionDao()
            .save(new SessionRecord(null, LocalDateTime.now(), null, 0, 0, 0, null));
        Optional<ExerciseRecord> last = daoFactory.createResultDao()
            .findLastExerciseBySessionId(session.id());
        assertThat(last).isEmpty();
    }

    @Test
    void shouldReturnLastExerciseForSession() throws Exception {
        SessionRecord session = daoFactory.createSessionDao()
            .save(new SessionRecord(null, LocalDateTime.now(), null, 0, 0, 0, null));
        ExerciseRecord ex1 = daoFactory.createExerciseDao()
            .save(new ExerciseRecord(null, "INTERVAL", 1, "{\"notes\":[60,67]}", "[60,67]", null));
        ExerciseRecord ex2 = daoFactory.createExerciseDao()
            .save(new ExerciseRecord(null, "INTERVAL", 1, "{\"notes\":[62,69]}", "[62,69]", null));
        daoFactory.createResultDao().save(
            new ResultRecord(null, session.id(), ex1.id(), "[60,67]", true, null));
        daoFactory.createResultDao().save(
            new ResultRecord(null, session.id(), ex2.id(), "[62,69]", true, null));

        Optional<ExerciseRecord> last = daoFactory.createResultDao()
            .findLastExerciseBySessionId(session.id());

        assertThat(last).isPresent();
        assertThat(last.get().id()).isEqualTo(ex2.id());
        assertThat(last.get().question()).isEqualTo("{\"notes\":[62,69]}");
    }
}
