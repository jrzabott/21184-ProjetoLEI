package pt.uab.musicaltrainer.e2e.steps;

import com.codeborne.selenide.Condition;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import pt.uab.musicaltrainer.api.GenerateRequest;
import pt.uab.musicaltrainer.e2e.pages.SessionEndPage;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.*;

/** Steps para session-end.feature: resumo de sessao de pratica e pontuada. */
public class SessionEndSteps {

    @Autowired TestRestTemplate rest;

    final SessionEndPage page = new SessionEndPage();

    /**
     * Prepara um resumo de pratica injectando contadores em sessionStorage
     * e navegando directamente para session-end.html.
     */
    private void loadPracticeEnd(int totalExercises, int correct) {
        open("/index.html");
        executeJavaScript(
            "sessionStorage.setItem('mt_mode',     JSON.stringify('practice'));" +
            "sessionStorage.setItem('mt_counters', JSON.stringify({" +
            "  correct:" + correct + ", incorrect:" + (totalExercises - correct) + ", total:" + totalExercises +
            "}));"
        );
        open("/session-end.html");
        page.title().shouldBe(Condition.visible);
    }

    @Given("que o utilizador concluiu uma sessão de prática")
    public void completedPractice() { loadPracticeEnd(0, 0); }

    @Given("que o utilizador concluiu uma sessão de prática com exercícios")
    public void completedPracticeWithExercises() { loadPracticeEnd(5, 4); }

    @Given("que a página de resumo está carregada")
    public void sessionEndLoaded() { loadPracticeEnd(3, 2); }

    /**
     * Cria uma sessao pontuada via API e simula o que exercise.html gravaria
     * em sessionStorage ao chamar endSession (o SessionResponse).
     */
    @Given("que o utilizador concluiu uma sessão pontuada")
    public void completedScoredSession() {
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

        // injeta o resultado em sessionStorage tal como exercise.html faria
        open("/index.html");
        executeJavaScript(
            "sessionStorage.setItem('mt_mode', JSON.stringify('session'));" +
            "sessionStorage.setItem('mt_session_result', JSON.stringify({" +
            "  totalExercises:1, correctAnswers:1, incorrectAnswers:0," +
            "  accuracy:1.0, startedAt:'" + Instant.now() + "'" +
            "}));"
        );
        open("/session-end.html");
        page.title().shouldBe(Condition.visible);
    }

    @Then("o título do resumo é {string}")
    public void titleIs(String expected) {
        page.title().shouldHave(Condition.text(expected));
    }

    @Then("o banner de prática no resumo está visível")
    public void practiceBannerVisible() {
        page.practiceBanner().shouldNotHave(Condition.cssClass("hidden"));
    }

    @Then("o banner de prática no resumo não está visível")
    public void practiceBannerNotVisible() {
        page.practiceBanner().shouldHave(Condition.cssClass("hidden"));
    }

    @Then("os contadores de exercícios estão preenchidos")
    public void countersFilledIn() {
        // total deve ser diferente de "—" (valor inicial)
        page.statTotal().shouldNotHave(Condition.text("—"));
    }

    @Then("a barra de precisão tem largura maior que zero")
    public void accuracyBarNonZero() {
        page.accuracyBar().shouldBe(Condition.visible);
        // a largura e definida por style="width:N%" - verificar que N > 0
        String style = page.accuracyBar().getAttribute("style");
        // extrai o valor numerico do "width:N%"
        double pct = Double.parseDouble(
            style.replaceAll(".*width:\\s*([0-9.]+)%.*", "$1"));
        Assertions.assertThat(pct).as("precisao deve ser maior que zero").isGreaterThan(0.0);
    }

    @When("o utilizador clica em Voltar")
    public void clickBack() { page.clickBack(); }

    @When("o utilizador clica em Iniciar sessão no resumo")
    public void clickNewSession() { page.clickNewSession(); }
}
