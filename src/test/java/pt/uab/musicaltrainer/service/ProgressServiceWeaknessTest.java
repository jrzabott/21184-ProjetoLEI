package pt.uab.musicaltrainer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pt.uab.musicaltrainer.api.ProgressResponse;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.dto.ResultRecord;
import pt.uab.musicaltrainer.dto.SessionRecord;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "db.type=H2")
class ProgressServiceWeaknessTest {

    @Autowired
    private DaoFactory daoFactory;
    @Autowired
    private WeaknessHintProvider hintProvider;
    @Autowired
    private ObjectMapper objectMapper;
    private ProgressService service;

    @BeforeEach
    void setUp() { service = new ProgressService(daoFactory, hintProvider, objectMapper); }

    @Test
    void shouldReturnWeakestAreasAsList() throws Exception {
        // campo weakestAreas sempre presente - lista (pode estar vazia ou nao consoante histórico)
        ProgressResponse progress = service.buildProgress();
        assertThat(progress.weakestAreas()).isNotNull();
    }

    @Test
    void shouldIdentifyWeakChordPattern() throws Exception {
        SessionRecord session = daoFactory.createSessionDao()
            .save(new SessionRecord(null, LocalDateTime.now(), null, 3, 0, 3, null));
        String dimQuestion = "{\"root\":60,\"type\":\"DIMINISHED\"}";
        for (int i = 0; i < 3; i++) {
            ExerciseRecord ex = daoFactory.createExerciseDao()
                .save(new ExerciseRecord(null, "CHORD", 7, dimQuestion, "[60,63,66]", null));
            daoFactory.createResultDao().save(
                new ResultRecord(null, session.id(), ex.id(), "[0,0,0]", false, null));
        }
        ProgressResponse progress = service.buildProgress();
        assertThat(progress.weakestAreas()).isNotEmpty();
        ProgressResponse.WeakArea worst = progress.weakestAreas().get(0);
        assertThat(worst.exerciseType()).isEqualTo("CHORD");
        assertThat(worst.pattern()).isEqualTo("DIMINISHED");
        assertThat(worst.accuracy()).isEqualTo(0.0);
        assertThat(worst.totalAttempts()).isEqualTo(3);
        assertThat(worst.hint()).isNotBlank();
    }
}
