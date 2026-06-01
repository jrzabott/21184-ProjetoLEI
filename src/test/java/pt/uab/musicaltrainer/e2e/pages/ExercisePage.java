package pt.uab.musicaltrainer.e2e.pages;

import com.codeborne.selenide.SelenideElement;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Selenide.*;

/** Page object para exercise.html - fluxo de exercicio activo (F03, F05, F09). */
public class ExercisePage {

    public void open() { Selenide.open("/exercise.html"); }

    public SelenideElement description()    { return $("#exercise-description"); }
    public SelenideElement hint()           { return $("#exercise-hint"); }
    public SelenideElement listenBtn()      { return $("#btn-listen"); }
    public SelenideElement notesDisplay()   { return $("#notes-display"); }
    public SelenideElement notesHint()      { return $("#notes-hint"); }
    public SelenideElement clearBtn()       { return $("#btn-clear"); }
    public SelenideElement submitBtn()      { return $("#btn-submit"); }
    public SelenideElement feedbackArea()   { return $("#feedback-area"); }
    public SelenideElement feedbackPanel()  { return $("#feedback-panel"); }
    public SelenideElement nextBtn()        { return $("#btn-next"); }
    public SelenideElement endBtn()         { return $("#btn-end"); }
    public SelenideElement practiceBanner() { return $("#practice-banner"); }
    public SelenideElement sessionCounter() { return $("#session-counter"); }
    public SelenideElement key(int midi)    { return $("[data-midi='" + midi + "']"); }

    public void clickListen()      { listenBtn().click(); }
    public void clickClear()       { clearBtn().click(); }
    public void clickSubmit()      { submitBtn().click(); }
    public void clickNext()        { nextBtn().click(); }
    public void clickEnd()         { endBtn().click(); }
    public void clickKey(int midi) { key(midi).click(); }

    /**
     * Le as notas correctas do exercicio activo via sessionStorage e dispara mousedown+mouseup
     * em cada tecla correspondente. Simula uma resposta correcta sem precisar de saber
     * as notas antecipadamente - o backend devolve-as ao gerar o exercicio e o frontend
     * guarda-as em mt_exercise.
     */
    public void clickCorrectNotes() {
        executeJavaScript(
            "const ex = JSON.parse(sessionStorage.getItem('mt_exercise'));" +
            "if (!ex || !ex.notes) return;" +
            "ex.notes.forEach(function(midi) {" +
            "  const k = document.querySelector('[data-midi=\"' + midi + '\"]');" +
            "  if (k) {" +
            "    k.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));" +
            "    k.dispatchEvent(new MouseEvent('mouseup',   {bubbles: true}));" +
            "  }" +
            "});"
        );
    }
}
