package pt.uab.musicaltrainer.e2e.pages;

import com.codeborne.selenide.SelenideElement;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Selenide.*;

/** Page object para index.html - landing page e modo sandbox (F08). */
public class IndexPage {

    /** Navega para index.html. Usa Selenide.open() explicitamente para evitar ambiguidade com o nome do metodo. */
    public void open() { Selenide.open("/index.html"); }

    public SelenideElement typeBtn(String dataType) { return $(".type-btn[data-type='" + dataType + "']"); }
    public SelenideElement practiceBtn()    { return $("#btn-practice"); }
    public SelenideElement sessionBtn()     { return $("#btn-session"); }
    public SelenideElement keyboard()       { return $("#sandbox-keyboard"); }
    public SelenideElement notesDisplay()   { return $("#sandbox-notes"); }
    public SelenideElement intervalDisplay(){ return $("#sandbox-interval"); }
    public SelenideElement helpBtn()        { return $("#help-btn"); }
    public SelenideElement helpModal()      { return $("#help-modal"); }
    public SelenideElement modalClose()     { return $("#modal-close"); }
    public SelenideElement orphanBanner()   { return $("#orphan-banner"); }
    public SelenideElement orphanEnd()      { return $("#orphan-end"); }
    public SelenideElement key(int midi)    { return $("[data-midi='" + midi + "']"); }

    public void clickType(String dataType) { typeBtn(dataType).click(); }
    public void clickPractice()            { practiceBtn().click(); }
    public void clickSession()             { sessionBtn().click(); }
    public void clickHelp()                { helpBtn().click(); }
    public void closeModal()               { modalClose().click(); }
    public void clickKey(int midi)         { key(midi).click(); }

    /**
     * Injeta sessao activa no sessionStorage e recarrega para o JS detectar o estado.
     * A pagina so le sessionStorage ao carregar - precisa de refresh apos injectar.
     */
    public void injectActiveSession() {
        Selenide.open("/index.html");
        executeJavaScript(
            "sessionStorage.setItem('mt_mode',       JSON.stringify('session'));" +
            "sessionStorage.setItem('mt_session_id', JSON.stringify(42));"
        );
        Selenide.refresh();
    }
}
