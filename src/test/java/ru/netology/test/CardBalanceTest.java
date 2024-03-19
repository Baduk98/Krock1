package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.netology.page.CardBalancePage;
import ru.netology.page.LoginPage;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.data.UserInfo.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardBalanceTest {
    CardBalancePage cardBalancePage;

    @BeforeEach
    void setup() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        Configuration.browserCapabilities = options;


        var loginPage = open("http://Localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        cardBalancePage = verificationPage.validVerify(verificationCode);

    }




    @Test
    void shouldTransferFromFirstToSecond() {
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = cardBalancePage.getCardBalance(firstCardInfo);
        var secondCardBalance = cardBalancePage.getCardBalance(secondCardInfo);
        var amount = generateValidAmount(firstCardBalance);
        var expectedDBalanceFirstCard = firstCardBalance - amount;
        var expectedDBalanceSecondCard = secondCardBalance + amount;
        var transferPage = cardBalancePage.selectCardToTransfer(secondCardInfo);
        cardBalancePage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        var actualBalanceFirstCard = cardBalancePage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = cardBalancePage.getCardBalance(secondCardInfo);
        assertEquals(expectedDBalanceFirstCard, actualBalanceFirstCard);
        assertEquals(expectedDBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    void shouldGetErrormessageIfAbountMore() {
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = cardBalancePage.getCardBalance(firstCardInfo);
        var secondCardBalance = cardBalancePage.getCardBalance(secondCardInfo);
        var amount = generateInValidAmount(secondCardBalance);
        var transferPage = cardBalancePage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
        var actualBalanceFirstCard = cardBalancePage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = cardBalancePage.getCardBalance(secondCardInfo);
        assertEquals(firstCardBalance, actualBalanceFirstCard);
        assertEquals(secondCardBalance, actualBalanceSecondCard);
    }
}