package pt.uab.musicaltrainer.config;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "db.type=H2")
class DataSourceConfigTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDataSourceBeanExists() {
        assertNotNull(dataSource);
    }

    @Test
    void testDataSourceCanGetConnection() throws Exception {
        var connection = dataSource.getConnection();
        assertNotNull(connection);
        assertFalse(connection.isClosed());
        connection.close();
    }
}
