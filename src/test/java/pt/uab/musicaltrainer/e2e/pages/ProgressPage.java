package pt.uab.musicaltrainer.e2e.pages;

import com.codeborne.selenide.SelenideElement;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Selenide.*;

/** Page object para progress.html - dashboard de progresso (F07). */
public class ProgressPage {

    public void open() { Selenide.open("/progress.html"); }

    public SelenideElement emptyState()    { return $("#empty-state"); }
    public SelenideElement emptyMsg()      { return $("#empty-msg"); }
    public SelenideElement startBtn()      { return $("#btn-start"); }
    public SelenideElement dashboard()     { return $("#dashboard"); }
    public SelenideElement globalPct()     { return $("#global-pct"); }
    public SelenideElement globalBar()     { return $("#global-bar"); }
    public SelenideElement byType()        { return $("#by-type"); }
    public SelenideElement recentSection() { return $("#recent-section"); }
    public SelenideElement backLink()      { return $("a[href='index.html']"); }

    public void clickStart() { startBtn().click(); }
    public void clickBack()  { backLink().click(); }
}
