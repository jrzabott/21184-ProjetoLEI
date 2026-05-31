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
        // espera que notesHint seja definido — confirma que notesQueue foi criada
        // (notesQueue e criada imediatamente apos descEl.textContent no mesmo bloco sync,
        // mas notesHint e o ultimo elemento a ser definido e serve de sentinela seguro)
        page.notesHint().shouldNotBe(Condition.empty);
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

    @Then("o painel de feedback tem classe incorrect")
    public void feedbackIsIncorrect() {
        page.feedbackPanel().shouldHave(Condition.cssClass("incorrect"));
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

    /**
     * RED: verifica que o selector de timbre existe com as 4 opcoes esperadas.
     * Antes da impl: o elemento #timbre-selector nao existe — o teste falha imediatamente.
     */
    @Then("deve existir um selector de timbre com as opcoes sine triangle sawtooth e piano")
    public void timbreSelectorExists() {
        com.codeborne.selenide.Selenide.$$("#timbre-selector input[type='radio']")
            .shouldHave(com.codeborne.selenide.CollectionCondition.size(4));
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='sine']").shouldBe(Condition.exist);
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='triangle']").shouldBe(Condition.exist);
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='sawtooth']").shouldBe(Condition.exist);
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='piano']").shouldBe(Condition.exist);
    }

    /**
     * Toca apenas a nota alvo (ultima nota do exercicio de intervalo), sem tocar a raiz.
     * Usa clickKey() via WebDriver Actions — JS dispatch nao funciona em headless Chrome.
     */
    @When("o utilizador toca apenas a nota alvo do intervalo")
    public void clickOnlyTargetNote() {
        Long targetMidi = (Long) executeJavaScript(
            "const ex = JSON.parse(sessionStorage.getItem('mt_exercise'));" +
            "if (!ex || !ex.notes || ex.notes.length < 2) return null;" +
            "return ex.notes[ex.notes.length - 1];"
        );
        if (targetMidi != null) page.clickKey(targetMidi.intValue());
    }

    /**
     * Toca a ultima nota do exercicio (que nao e a raiz) sem fazer prepend.
     * Usado para verificar que tipos que nao sao INTERVAL nao activam o prepend automatico.
     */
    @When("o utilizador clica na tecla MIDI que nao e raiz")
    public void clickNonRootKey() {
        Long nonRoot = (Long) executeJavaScript(
            "const ex = JSON.parse(sessionStorage.getItem('mt_exercise'));" +
            "if (!ex || !ex.notes || ex.notes.length < 2) return null;" +
            "return ex.notes[ex.notes.length - 1];"
        );
        if (nonRoot != null) page.clickKey(nonRoot.intValue());
    }

    /**
     * RED feat/77: verifica que o botao Ouvir nao fica desactivado ou escondido
     * apos a reproducao da sequencia. Antes do fix, setTimeout() podia criar
     * race conditions em abas inactivas que deixavam o botao num estado errado.
     * Aguarda o dobro da duracao maxima esperada (5 notas * 300ms + 400ms folga = 1900ms).
     */
    @Then("o botão Ouvir deve estar disponivel apos a reproducao")
    public void listenBtnAvailableAfterPlayback() {
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        page.listenBtn().shouldBe(Condition.visible);
        page.listenBtn().shouldNotBe(Condition.disabled);
    }

    /**
     * Toca 3 notas em rapida sucessao via WebDriver Actions.
     * Verifica que o scheduling nativo Web Audio (feat/77) nao impede o registo de input.
     * Notas C3(48), D3(50), E3(52) existem em qualquer configuracao de teclado C2-C6.
     */
    @When("o utilizador toca 3 notas em rapida sucessao")
    public void clickThreeNotesFast() {
        page.clickKey(48);
        page.clickKey(50);
        page.clickKey(52);
    }

    @Then("o painel de notas do exercício contém {int} notas registadas")
    public void notesPanelHasNNotes(int expectedCount) {
        String text = page.notesDisplay().getText();
        int count = text.equals("—") ? 0 : text.split(" - ").length;
        org.assertj.core.api.Assertions.assertThat(count)
            .as("painel deve ter %d notas registadas, mas mostra: '%s'", expectedCount, text)
            .isEqualTo(expectedCount);
    }

    @Then("o radio button sine deve estar seleccionado")
    public void sineSelectedByDefault() {
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='sine']")
            .shouldBe(Condition.checked);
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
