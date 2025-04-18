package ru.netology.testmode.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import ru.netology.testmode.data.DataGenerator;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class AuthorizationTest {

    @BeforeAll
    static void setupAll() {
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 30000;
        Configuration.headless = true;
    }

    @BeforeEach
    void openPage() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Успешная авторизация активного пользователя")
    void shouldLoginWithValidCredentials() {
        var validUser = DataGenerator.registerUser("active");

        $("[data-test-id=login] input").setValue(validUser.getLogin());
        $("[data-test-id=password] input").setValue(validUser.getPassword());
        $("[data-test-id=action-login]").click();

        $("h2").shouldBe(visible)
                .shouldHave(exactText("Личный кабинет"));
    }

    @Test
    @DisplayName("Ошибка авторизации с неверным логином")
    void shouldShowErrorOnInvalidLogin() {
        var validUser = DataGenerator.registerUser("active");
        var invalidLogin = DataGenerator.generateRandomLogin();

        $("[data-test-id=login] input").setValue(invalidLogin);
        $("[data-test-id=password] input").setValue(validUser.getPassword());
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification]")
                .shouldBe(visible)
                .shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Ошибка авторизации заблокированного пользователя")
    void shouldShowErrorOnBlockedUser() {
        var blockedUser = DataGenerator.registerUser("blocked");

        $("[data-test-id=login] input").setValue(blockedUser.getLogin());
        $("[data-test-id=password] input").setValue(blockedUser.getPassword());
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification]")
                .shouldBe(visible)
                .shouldHave(text("Ошибка! Пользователь заблокирован"));
    }

    @Test
    @DisplayName("Ошибка авторизации с неверным паролем")
    void shouldShowErrorOnInvalidPassword() {
        var validUser = DataGenerator.registerUser("active");
        var invalidPassword = DataGenerator.generateRandomPassword();

        $("[data-test-id=login] input").setValue(validUser.getLogin());
        $("[data-test-id=password] input").setValue(invalidPassword);
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification]")
                .shouldBe(visible)
                .shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }
}
