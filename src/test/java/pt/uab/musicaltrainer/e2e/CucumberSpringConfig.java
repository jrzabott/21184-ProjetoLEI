package pt.uab.musicaltrainer.e2e;

import com.codeborne.selenide.Configuration;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.codeborne.selenide.Selenide.closeWebDriver;

/**
 * Configura o contexto Spring e o browser Selenide para cada cenario.
 *
 * @SpringBootTest arranca o backend completo (H2 in-memory) numa porta aleatoria.
 * O frontend e servido via spring.web.resources.static-locations=file:frontend/
 * tal como em desenvolvimento - sem mocks, stack completa.
 *
 * Browser headless por defeito. Para ver o Chrome durante desenvolvimento:
 *   mvn test -Dtest=CucumberRunnerTest -Dselenide.headless=false
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfig {

    @LocalServerPort
    int port;

    @Before
    public void configureBrowser() {
        Configuration.browser      = "chrome";
        Configuration.headless     = Boolean.parseBoolean(System.getProperty("selenide.headless", "true"));
        Configuration.baseUrl      = "http://localhost:" + port;
        Configuration.timeout             = 8000;
        Configuration.pageLoadTimeout     = 20000;
        Configuration.reopenBrowserOnFail = true;
    }

    @After
    public void closeBrowser() {
        closeWebDriver();
    }
}
