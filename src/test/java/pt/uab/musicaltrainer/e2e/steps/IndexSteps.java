package pt.uab.musicaltrainer.e2e.steps;

import com.codeborne.selenide.Condition;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pt.uab.musicaltrainer.e2e.pages.ExercisePage;
import pt.uab.musicaltrainer.e2e.pages.IndexPage;

import static com.codeborne.selenide.Selenide.title;

/** Steps para index.feature: landing, sandbox, modal de ajuda, sessoes orfas. */
public class IndexSteps {

    final IndexPage    indexPage    = new IndexPage();
    final ExercisePage exercisePage = new ExercisePage();

    @Then("o título da página é {string}")
    public void titleContains(String expected) {
        org.assertj.core.api.Assertions.assertThat(title()).contains(expected);
    }

    @Then("o botão Praticar está visível")
    public void practiceBtnVisible() { indexPage.practiceBtn().shouldBe(Condition.visible); }

    @Then("o botão Iniciar sessão está visível")
    public void sessionBtnVisible() { indexPage.sessionBtn().shouldBe(Condition.visible); }

    @Then("o teclado sandbox está visível")
    public void keyboardVisible() { indexPage.keyboard().shouldBe(Condition.visible); }

    @Then("o tipo {string} está activo")
    public void typeActive(String type) {
        indexPage.typeBtn(type).shouldHave(Condition.cssClass("active"));
    }

    @Then("o tipo {string} não está activo")
    public void typeNotActive(String type) {
        indexPage.typeBtn(type).shouldNotHave(Condition.cssClass("active"));
    }

    // Cucumber trata @When e @And como equivalentes em runtime - uma unica definicao basta
    @When("o utilizador selecciona o tipo {string}")
    public void selectType(String type) { indexPage.clickType(type); }

    @When("o utilizador clica na tecla MIDI {int}")
    public void clickMidi(int midi) { indexPage.clickKey(midi); }

    @Then("o painel de notas sandbox contém {string}")
    public void notesPanelContains(String text) {
        indexPage.notesDisplay().shouldHave(Condition.partialText(text));
    }

    @Then("o painel de intervalo sandbox não está vazio")
    public void intervalNotEmpty() {
        indexPage.intervalDisplay().shouldNotBe(Condition.empty);
    }

    @When("o utilizador clica no botão de ajuda")
    public void clickHelp() { indexPage.clickHelp(); }

    @And("o utilizador clicou no botão de ajuda")
    public void clickedHelp() { indexPage.clickHelp(); }

    @Then("o modal de ajuda está visível")
    public void modalVisible() {
        indexPage.helpModal().shouldHave(Condition.cssClass("visible"));
    }

    @When("o utilizador fecha o modal de ajuda")
    public void closeModal() { indexPage.closeModal(); }

    @Then("o modal de ajuda não está visível")
    public void modalNotVisible() {
        indexPage.helpModal().shouldNotHave(Condition.cssClass("visible"));
    }

    @When("o utilizador clica em Praticar")
    public void clickPractice() { indexPage.clickPractice(); }

    @When("o utilizador clica em Iniciar sessão")
    public void clickSession() { indexPage.clickSession(); }

    @Given("que existe uma sessão activa no storage")
    public void activeSessionInStorage() { indexPage.injectActiveSession(); }

    @Then("o banner de sessão em curso está visível")
    public void orphanBannerVisible() {
        indexPage.orphanBanner().shouldNotHave(Condition.cssClass("hidden"));
    }

    @When("o utilizador termina a sessão em curso")
    public void endOrphanSession() { indexPage.orphanEnd().click(); }

    @Then("o banner de sessão em curso não está visível")
    public void orphanBannerNotVisible() {
        indexPage.orphanBanner().shouldHave(Condition.cssClass("hidden"));
    }

    /**
     * RED: selecciona um timbre pelo radio button no index.html.
     * Antes da impl: #timbre-selector nao existe em index.html — o passo falha.
     */
    @When("o utilizador selecciona o timbre {string}")
    public void selectTimbre(String timbre) {
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='" + timbre + "']").click();
    }

    /**
     * RED: verifica que o timbre persiste apos navegar para exercise.html.
     * Antes da impl: mt_timbre nao existe em sessionStorage — radio button nao e seleccionado.
     */
    @Then("deve existir um selector de timbre na pagina inicial com as opcoes sine triangle sawtooth e piano")
    public void timbreSelectorExistsOnIndex() {
        com.codeborne.selenide.Selenide.$$("#timbre-selector input[type='radio']")
            .shouldHave(com.codeborne.selenide.CollectionCondition.size(4));
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='sine']").shouldBe(Condition.exist);
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='triangle']").shouldBe(Condition.exist);
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='sawtooth']").shouldBe(Condition.exist);
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='piano']").shouldBe(Condition.exist);
    }

    @Then("o timbre {string} deve estar seleccionado na pagina de exercicio")
    public void timbreSelectedInExercise(String timbre) {
        com.codeborne.selenide.Selenide.$("#timbre-selector input[value='" + timbre + "']")
            .shouldBe(Condition.checked);
    }
}
