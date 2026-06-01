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
    public void clickKey(int midi) {
        SelenideElement k = key(midi).scrollIntoView(true);
        // Actions gera eventos reais de mouse (mousedown+mouseup) via WebDriver,
        // ao contrario de JS dispatch que pode ser ignorado pelo browser em alguns contextos
        com.codeborne.selenide.Selenide.actions()
            .moveToElement(k.getWrappedElement())
            .clickAndHold()
            .release()
            .perform();
    }

    /**
     * Le as notas correctas do exercicio activo via sessionStorage e usa clickKey() por cada nota.
     * clickKey() usa WebDriver Actions que gera eventos reais de mouse, ao contrario de JS dispatch
     * que pode ser bloqueado pelo browser em contexto headless.
     */
    @SuppressWarnings("unchecked")
    public void clickCorrectNotes() {
        Object result = executeJavaScript(
            "const ex = JSON.parse(sessionStorage.getItem('mt_exercise'));" +
            "if (!ex || !ex.notes) return null;" +
            "return ex.notes;"
        );
        if (result == null) return;
        java.util.List<Long> notes = (java.util.List<Long>) result;
        for (Long midi : notes) {
            clickKey(midi.intValue());
        }
    }
}
