package pt.uab.musicaltrainer.e2e.steps;

import com.codeborne.selenide.Condition;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import pt.uab.musicaltrainer.api.GenerateRequest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import pt.uab.musicaltrainer.e2e.pages.ExercisePage;
import pt.uab.musicaltrainer.e2e.pages.IndexPage;
import pt.uab.musicaltrainer.e2e.pages.ProgressPage;
import pt.uab.musicaltrainer.e2e.pages.SessionEndPage;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;

/**
 * Steps transversais a multiplas features: navegacao, setup de DB, verificacoes comuns.
 * Nao define steps que possam colidir com steps especificos de outras classes.
 */
public class SharedSteps {

    @Autowired TestRestTemplate rest;
    @Autowired DataSource       dataSource;

    final IndexPage      indexPage      = new IndexPage();
    final ExercisePage   exercisePage   = new ExercisePage();
    final SessionEndPage sessionEndPage = new SessionEndPage();
    final ProgressPage   progressPage   = new ProgressPage();

    // --- navegacao basica ---

    @Given("que o utilizador abre a aplicação")
    public void openApp() { indexPage.open(); }

    @Given("que o utilizador está na página de progresso")
    public void openProgressPage() { progressPage.open(); }

    // --- verificacoes de navegacao ---

    @Then("a página de exercício está visível")
    public void exercisePageVisible() {
        exercisePage.description().shouldBe(Condition.visible);
    }

    @Then("a página de resumo está visível")
    public void sessionEndPageVisible() {
        sessionEndPage.title().shouldBe(Condition.visible);
    }

    @Then("o botão Iniciar sessão da página inicial está visível")
    public void indexSessionBtnVisible() {
        indexPage.sessionBtn().shouldBe(Condition.visible);
    }

    // --- verificacoes de banner de pratica (referencia ExercisePage) ---

    @Then("o banner de prática está visível no exercício")
    public void practiceBannerVisible() {
        exercisePage.practiceBanner().shouldNotHave(Condition.cssClass("hidden"));
    }

    @Then("o banner de prática não está visível no exercício")
    public void practiceBannerNotVisible() {
        exercisePage.practiceBanner().shouldHave(Condition.cssClass("hidden"));
    }

    // --- setup de base de dados via REST API ---

    /**
     * Cria uma sessao completa via REST: start -> generate -> answer -> end.
     * Garante que a pagina de progresso tem dados para mostrar.
     */
    @Given("que existem sessões registadas")
    public void createSessionWithData() {
        var session = rest.postForObject("/api/sessions/start", Map.of(), Map.class);
        long sessionId = ((Number) session.get("id")).longValue();

        var ex = rest.postForObject("/api/exercises/generate",
            new GenerateRequest("INTERVAL", 1, sessionId), Map.class);
        long exerciseId = ((Number) ex.get("exerciseId")).longValue();
        List<?> notesList = (List<?>) ex.get("notes");
        int[] notes = notesList.stream().mapToInt(n -> ((Number) n).intValue()).toArray();

        rest.postForObject("/api/exercises/answer",
            Map.of("exerciseId", exerciseId, "sessionId", sessionId,
                   "notes", notes, "responseTimeMs", 1000),
            Map.class);

        rest.postForObject("/api/sessions/" + sessionId + "/end", Map.of(), Map.class);
    }

    /**
     * Apaga todos os dados das tabelas de teste.
     * H2 in-memory nao faz reset automatico entre cenarios - fazemos aqui.
     * Ordem: results primeiro (tem FKs para sessions e exercises).
     */
    @Given("que não existem sessões registadas")
    public void clearAllData() {
        // apaga na ordem certa: results tem FK para sessions e exercises
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM results");
            stmt.execute("DELETE FROM exercises");
            stmt.execute("DELETE FROM sessions");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao limpar dados de teste", e);
        }
    }
}
