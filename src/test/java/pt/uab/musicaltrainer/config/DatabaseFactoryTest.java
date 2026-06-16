package pt.uab.musicaltrainer.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseFactoryTest {

    @SpringBootTest
    @TestPropertySource(properties = "db.type=H2")
    @Nested
    class H2StrategyTests {
        @Autowired
        private DatabaseFactory databaseFactory;

        @Test
        void testH2StrategySelected() {
            DatabaseStrategy strategy = databaseFactory.getStrategy();
            assertNotNull(strategy);
            assertEquals("H2", strategy.getName());
            assertEquals("org.h2.Driver", strategy.getDriverClassName());
            assertTrue(strategy.getUrl().contains("jdbc:h2:mem"));
        }
    }

    @SpringBootTest
    @TestPropertySource(properties = "db.type=SQLITE")
    @Nested
    class SqliteStrategyTests {
        @Autowired
        private DatabaseFactory databaseFactory;

        @Test
        void testSqliteStrategySelected() {
            DatabaseStrategy strategy = databaseFactory.getStrategy();
            assertNotNull(strategy);
            assertEquals("SQLite", strategy.getName());
            assertEquals("org.sqlite.JDBC", strategy.getDriverClassName());
            assertTrue(strategy.getUrl().contains("jdbc:sqlite"));
        }
    }

    @SpringBootTest
    @TestPropertySource(properties = {
        "db.type=POSTGRES",
        "db.postgres.host=localhost",
        "db.postgres.port=5432",
        "db.postgres.database=test_db",
        "db.postgres.username=testuser",
        "db.postgres.password=testpass"
    })
    @Nested
    class PostgresStrategyTests {
        @Autowired
        private DatabaseFactory databaseFactory;

        @Test
        void testPostgresStrategySelected() {
            DatabaseStrategy strategy = databaseFactory.getStrategy();
            assertNotNull(strategy);
            assertEquals("PostgreSQL", strategy.getName());
            assertEquals("org.postgresql.Driver", strategy.getDriverClassName());
            assertTrue(strategy.getUrl().contains("jdbc:postgresql"));
            assertTrue(strategy.getUrl().contains("localhost:5432/test_db"));
            assertEquals("testuser", strategy.getUsername());
            assertEquals("testpass", strategy.getPassword());
        }
    }

    @Nested
    class InvalidTypeTests {
        // Teste unitário directo - não precisa de contexto Spring
        @Test
        void testInvalidDatabaseTypeThrowsException() {
            DatabaseFactory factory = new DatabaseFactory(
                "INVALID_TYPE", "localhost", 5432, "db", "user", "", "musical-trainer.db"
            );
            assertThrows(IllegalArgumentException.class, factory::getStrategy);
        }
    }

    @Nested
    class SqliteStrategyWithCustomPathTests {
        @Test
        void testSqliteStrategyWithCustomPath() {
            DatabaseFactory factory = new DatabaseFactory(
                "SQLITE", "localhost", 5432, "db", "user", "", "/data/musical-trainer.db"
            );
            DatabaseStrategy strategy = factory.getStrategy();
            assertEquals("jdbc:sqlite:/data/musical-trainer.db", strategy.getUrl());
        }

        @Test
        void testSqliteStrategyWithDefaultPath() {
            DatabaseFactory factory = new DatabaseFactory(
                "SQLITE", "localhost", 5432, "db", "user", "", "musical-trainer.db"
            );
            DatabaseStrategy strategy = factory.getStrategy();
            assertEquals("jdbc:sqlite:musical-trainer.db", strategy.getUrl());
        }
    }
}
