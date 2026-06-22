package pt.uab.musicaltrainer.dao;

import pt.uab.musicaltrainer.dto.SessionRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * Regressão: setTimestamp() no driver SQLite JDBC gravava o valor como epoch em
 * milissegundos em vez de texto ISO, causando ParseException ao ler de volta.
 * Reproduz exactamente o erro em produção (Render/SQLite).
 */
@SpringBootTest
@TestPropertySource(properties = {
    "db.type=SQLITE",
    "db.sqlite.path=./target/test-sqlite-session-timestamps.db"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SessionDaoSqliteIT {

    @Autowired
    private DataSource dataSource;

    private SessionDao sessionDao;

    @BeforeEach
    void setUp() {
        sessionDao = new SessionDao(dataSource);
    }

    @AfterAll
    static void cleanup() {
        new File("./target/test-sqlite-session-timestamps.db").delete();
    }

    @Test
    @Order(1)
    void startTime_deve_ser_preservado_no_round_trip_sqlite() throws Exception {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        SessionRecord session = new SessionRecord(null, now, null, 0, 0, 0, now);

        SessionRecord saved = sessionDao.save(session);

        assertThat(saved).isNotNull();
        assertThat(saved.id()).isGreaterThan(0);
        assertThat(saved.startTime()).isEqualTo(now);
    }

    @Test
    @Order(2)
    void endTime_deve_ser_preservado_no_update_sqlite() throws Exception {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        SessionRecord session = new SessionRecord(null, start, null, 0, 0, 0, start);
        SessionRecord saved = sessionDao.save(session);

        LocalDateTime end = start.plusMinutes(5);
        SessionRecord updated = new SessionRecord(saved.id(), start, end, 3, 2, 1, start);
        sessionDao.update(updated);

        SessionRecord retrieved = sessionDao.findById(saved.id()).orElseThrow();
        assertThat(retrieved.endTime()).isEqualTo(end);
        assertThat(retrieved.totalExercises()).isEqualTo(3);
    }

    @Test
    @Order(3)
    void findAll_deve_retornar_timestamps_validos_sqlite() throws Exception {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        sessionDao.save(new SessionRecord(null, now, null, 0, 0, 0, now));
        sessionDao.save(new SessionRecord(null, now.plusMinutes(1), null, 0, 0, 0, now));

        var all = sessionDao.findAll();

        assertThat(all).isNotEmpty();
        assertThat(all).allSatisfy(s -> assertThat(s.startTime()).isNotNull());
    }
}
