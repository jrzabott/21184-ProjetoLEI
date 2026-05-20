package pt.uab.musicaltrainer.e2e.steps;

import com.codeborne.selenide.Condition;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pt.uab.musicaltrainer.e2e.pages.ProgressPage;

import static com.codeborne.selenide.Selenide.*;

/** Steps para progress.feature: dashboard de progresso (F07). */
public class ProgressSteps {

    final ProgressPage page = new ProgressPage();

    @When("o utilizador abre a página de progresso")
    public void openProgressPage() {
        page.open();
        waitForLoad();
    }

    /**
     * Aguarda que a pagina de progresso termine de carregar.
     * O estado inicial tem h2 com "A carregar..." - muda apos a chamada ao backend.
     * Qualquer outro texto significa que o JS terminou de processar.
     */
    private void waitForLoad() {
        page.emptyState().$("h2").shouldNotHave(Condition.text("A carregar..."));
    }

    @Then("o estado vazio está visível")
    public void emptyStateVisible() {
        page.emptyState().shouldNotHave(Condition.cssClass("hidden"));
    }

    @Then("a mensagem de estado vazio contém {string}")
    public void emptyMsgContains(String text) {
        page.emptyMsg().shouldHave(Condition.text(text));
    }

    @Then("o botão Iniciar sessão está visível no estado vazio")
    public void startBtnVisible() {
        page.startBtn().shouldBe(Condition.visible);
        page.startBtn().shouldNotHave(Condition.cssClass("hidden"));
    }

    @Then("o dashboard de progresso está visível")
    public void dashboardVisible() {
        page.dashboard().shouldNotHave(Condition.cssClass("hidden"));
    }

    @Then("a precisão global está preenchida")
    public void globalPctFilled() {
        page.globalPct().shouldNotHave(Condition.text("—"));
        page.globalPct().shouldNotBe(Condition.empty);
    }

    @Then("as barras por tipo estão visíveis")
    public void byTypeBarsVisible() {
        page.byType().shouldBe(Condition.visible);
        // deve existir pelo menos uma .progress-bar dentro do container
        page.byType().$(".progress-bar").shouldBe(Condition.visible);
    }

    @Then("a secção de sessões recentes está visível")
    public void recentSessionsVisible() {
        page.recentSection().shouldNotHave(Condition.cssClass("hidden"));
    }

    @When("o utilizador clica no link de início")
    public void clickBackLink() { page.clickBack(); }

    @When("o utilizador clica em Iniciar sessão no estado vazio")
    public void clickStartFromEmpty() { page.clickStart(); }
}
