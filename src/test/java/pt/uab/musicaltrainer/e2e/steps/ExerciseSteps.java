package pt.uab.musicaltrainer.e2e.steps;

import com.codeborne.selenide.Condition;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import pt.uab.musicaltrainer.e2e.ScenarioContext;
import pt.uab.musicaltrainer.e2e.pages.ExercisePage;

import static com.codeborne.selenide.Selenide.*;

/** Steps para exercise.feature: geração, notas, avaliação, navegação. */
public class ExerciseSteps {

    @Autowired ScenarioContext ctx;

    final ExercisePage page = new ExercisePage();

    /**
     * Injeta estado de modo pratica em sessionStorage antes de navegar para exercise.html.
     * Usar executeJavaScript requer estar num contexto de pagina da mesma origem primeiro.
     */
    @Given("que o utilizador configurou o modo prática com tipo {string}")
    public void configurePracticeMode(String type) {
        open("/index.html"); // estabelece a origem para sessionStorage
        executeJavaScript(
            "sessionStorage.setItem('mt_mode',       JSON.stringify('practice'));" +
            "sessionStorage.setItem('mt_type',        JSON.stringify('" + type + "'));" +
            "sessionStorage.setItem('mt_session_id',  JSON.stringify(0));" +
            "sessionStorage.setItem('mt_difficulty',  JSON.stringify(1));" +
            "sessionStorage.setItem('mt_counters',    JSON.stringify({correct:0,incorrect:0,total:0}));"
        );
    }

    @And("está na página de exercício")
    public void navigateToExercise() {
        page.open();
        // espera que o exercicio carregue - o texto inicial muda apos a chamada ao backend
        page.description().shouldNotHave(Condition.text("A carregar exercício..."));
        ctx.lastExerciseDescription = page.description().getText();
    }

    @Then("um exercício está visível com descrição não vazia")
    public void exerciseLoaded() {
        page.description().shouldBe(Condition.visible);
        page.description().shouldNotBe(Condition.empty);
    }

    @Then("o botão Ouvir está disponível")
    public void listenBtnAvailable() { page.listenBtn().shouldBe(Condition.visible); }

    @Then("o painel de notas mostra o traço inicial")
    public void notesPanelShowsDash() {
        page.notesDisplay().shouldHave(Condition.text("—"));
    }

    @Then("o banner de prática está visível no ecrã de exercício")
    public void practiceBannerVisible() {
        page.practiceBanner().shouldNotHave(Condition.cssClass("hidden"));
    }

    @Then("o header de exercício mostra Prática")
    public void headerShowsPratica() {
        page.sessionCounter().shouldHave(Condition.partialText("Prática"));
    }

    @When("o utilizador clica na tecla MIDI {int} no exercício")
    public void clickKeyExercise(int midi) { page.clickKey(midi); }

    @Given("o utilizador clicou na tecla MIDI {int} no exercício")
    public void clickedKeyExercise(int midi) { page.clickKey(midi); }

    @Then("o painel de notas do exercício contém {string}")
    public void exerciseNotesPanelContains(String text) {
        page.notesDisplay().shouldHave(Condition.partialText(text));
    }

    @When("o utilizador clica em Limpar")
    public void clickClear() { page.clickClear(); }

    @When("o utilizador clica em Enviar resposta")
    public void clickSubmit() { page.clickSubmit(); }

    @Then("o painel de feedback está visível")
    public void feedbackVisible() {
        page.feedbackArea().shouldNotHave(Condition.cssClass("hidden"));
    }

    @Then("o painel tem classe correct ou incorrect")
    public void feedbackHasCorrectOrIncorrectClass() {
        String cls = page.feedbackPanel().getAttribute("class");
        Assertions.assertThat(cls)
            .as("painel de feedback deve ter classe 'correct' ou 'incorrect'")
            .matches(c -> c.contains("correct") || c.contains("incorrect"));
    }

    @When("o utilizador toca as notas correctas do exercício")
    public void clickCorrectNotes() { page.clickCorrectNotes(); }

    @Then("o painel de feedback tem classe correct")
    public void feedbackIsCorrect() {
        page.feedbackPanel().shouldHave(Condition.cssClass("correct"));
    }

    @Given("o utilizador enviou uma resposta qualquer")
    public void submitAnyAnswer() {
        // submete sem notas - o backend devolve incorrect, o que e suficiente para o teste
        page.clickSubmit();
        page.feedbackArea().shouldNotHave(Condition.cssClass("hidden"));
    }

    @When("o utilizador clica em Próximo exercício")
    public void clickNext() { page.clickNext(); }

    @Then("o painel de feedback não está visível")
    public void feedbackNotVisible() {
        page.feedbackArea().shouldHave(Condition.cssClass("hidden"));
    }

    @When("o utilizador clica em Terminar no exercício")
    public void clickEnd() { page.clickEnd(); }

    @When("o utilizador clica no botão Ouvir")
    public void clickListen() { page.clickListen(); }

    @Then("apenas uma tecla está destacada no teclado de exercício")
    public void onlyOneKeyHighlighted() {
        // verifica que Ouvir destaca apenas a nota raiz e nao todas as notas em sequencia
        // aguarda um curto periodo para o highlight ser aplicado apos o click
        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        int count = com.codeborne.selenide.Selenide.$$(".highlighted").size();
        org.assertj.core.api.Assertions.assertThat(count)
            .as("Ouvir deve destacar apenas 1 tecla (nota raiz) — nao todas em sequencia")
            .isLessThanOrEqualTo(1);
    }

    @Then("o painel de feedback nao contém numeros MIDI em bruto")
    public void feedbackHasNoRawMidi() {
        // verifica que o feedback mostra "C4, G4" e nao "[60, 67]"
        // antes do fix, result.explanation continha numeros MIDI brutos do backend
        String text = page.feedbackPanel().getText();
        org.assertj.core.api.Assertions.assertThat(text)
            .as("feedback nao deve conter numeros MIDI em bruto como [60] ou [48, 50, ...]")
            .doesNotMatch(".*\\[\\d[\\d,\\s]*\\].*");
    }

    @Given("que o utilizador configurou uma sessão pontuada com tipo {string}")
    public void configureScoredSession(String type) {
        open("/index.html");
        executeJavaScript(
            "sessionStorage.setItem('mt_type',       JSON.stringify('" + type + "'));" +
            "sessionStorage.setItem('mt_difficulty', JSON.stringify(1));"
        );
        $("#btn-session").click();
        // aguarda carregamento do exercicio
        page.description().shouldNotHave(Condition.text("A carregar exercício..."));
    }
}
