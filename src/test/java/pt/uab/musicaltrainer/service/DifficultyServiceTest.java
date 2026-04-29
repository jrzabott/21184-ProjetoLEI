package pt.uab.musicaltrainer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pt.uab.musicaltrainer.dao.DaoFactory;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "db.type=H2")
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
}
