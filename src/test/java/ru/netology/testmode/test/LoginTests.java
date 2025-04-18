package ru.netology.testmode.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class AuthorizationTest {

    private static final String SERVICE_URL = "http://localhost:9999";
    private static final String DASHBOARD_HEADER = "Личный кабинет";
    private static final String AUTH_ERROR = "Ошибка! Неверно указан логин или пароль";
    private static final String BLOCKED_ERROR = "Ошибка! Пользователь заблокирован";

    @BeforeAll
    static void configureTests() {
        Configuration.timeout = 8000;
        Configuration.headless = true; // Добавлен headless режим
    }

    @BeforeEach
    void openAuthPage() {
        open(SERVICE_URL);
    }

    @Test
    @DisplayName("Успешная авторизация активного пользователя")
    void successfulAuthorizationWithValidCredentials() {
        var validUser = TestDataHelper.getActiveUser();

        $("[data-test-id=login] input").setValue(validUser.getLogin());
        $("[data-test-id=password] input").setValue(validUser.getPassword());
        $("[data-test-id=action-login]").click();

        $("h2").shouldBe(visible)
                .shouldHave(exactText(DASHBOARD_HEADER));
    }

    @Test
    @DisplayName("Ошибка авторизации с неверным логином")
    void authorizationErrorWithInvalidLogin() {
        var validUser = TestDataHelper.getActiveUser();
        var invalidLogin = TestDataHelper.generateRandomLogin();

        $("[data-test-id=login] input").setValue(invalidLogin);
        $("[data-test-id=password] input").setValue(validUser.getPassword());
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification]")
                .shouldBe(visible)
                .shouldHave(text(AUTH_ERROR));
    }

    @Test
    @DisplayName("Ошибка авторизации заблокированного пользователя")
    void authorizationErrorForBlockedUser() {
        var blockedUser = TestDataHelper.getBlockedUser();

        $("[data-test-id=login] input").setValue(blockedUser.getLogin());
        $("[data-test-id=password] input").setValue(blockedUser.getPassword());
        $("[data-test-id=action-login]").pressEnter();

        $("[data-test-id=error-notification]")
                .shouldBe(visible)
                .shouldHave(text(BLOCKED_ERROR));
    }

    @Test
    @DisplayName("Ошибка авторизации с неверным паролем")
    void authorizationErrorWithInvalidPassword() {
        var validUser = TestDataHelper.getActiveUser();
        var invalidPassword = TestDataHelper.generateRandomPassword();

        $("[data-test-id=login] input").setValue(validUser.getLogin());
        $("[data-test-id=password] input").setValue(invalidPassword);
        $("[data-test-id=action-login]").click();

        $("[data-test-id=error-notification]")
                .shouldBe(visible)
                .shouldHave(text(AUTH_ERROR));
    }
}
