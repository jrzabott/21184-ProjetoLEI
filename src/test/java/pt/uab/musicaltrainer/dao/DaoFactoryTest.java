package pt.uab.musicaltrainer.dao;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "db.type=H2")
class DaoFactoryTest {

    @Autowired
    private DaoFactory daoFactory;

    @Test
    void testDaoFactoryCreatesSessionDao() {
        SessionDao sessionDao = daoFactory.createSessionDao();
        assertNotNull(sessionDao);
    }

    @Test
    void testDaoFactoryCreatesExerciseDao() {
        ExerciseDao exerciseDao = daoFactory.createExerciseDao();
        assertNotNull(exerciseDao);
    }

    @Test
    void testDaoFactoryCreatesResultDao() {
        ResultDao resultDao = daoFactory.createResultDao();
        assertNotNull(resultDao);
    }
}
