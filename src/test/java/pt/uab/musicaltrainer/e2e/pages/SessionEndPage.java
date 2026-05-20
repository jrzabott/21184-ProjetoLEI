package pt.uab.musicaltrainer.e2e.pages;

import com.codeborne.selenide.SelenideElement;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Selenide.*;

/** Page object para session-end.html - resumo de sessao. */
public class SessionEndPage {

    public void open() { Selenide.open("/session-end.html"); }

    public SelenideElement title()          { return $("#page-title"); }
    public SelenideElement practiceBanner() { return $("#practice-banner"); }
    public SelenideElement statTotal()      { return $("#stat-total"); }
    public SelenideElement statCorrect()    { return $("#stat-correct"); }
    public SelenideElement statIncorrect()  { return $("#stat-incorrect"); }
    public SelenideElement accuracyPct()    { return $("#accuracy-pct"); }
    public SelenideElement accuracyBar()    { return $("#accuracy-bar"); }
    public SelenideElement weakArea()       { return $("#weak-area"); }
    public SelenideElement backBtn()        { return $("#btn-back"); }
    public SelenideElement newSessionBtn()  { return $("#btn-new-session"); }

    public void clickBack()       { backBtn().click(); }
    public void clickNewSession() { newSessionBtn().click(); }
}
