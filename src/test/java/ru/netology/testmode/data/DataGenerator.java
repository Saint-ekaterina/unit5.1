package ru.netology.testmode.data;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import java.util.HashMap;

public final class DataGenerator {

    private static final Map<String, Object> API_CONFIG = new HashMap<>();
    static {
        API_CONFIG.put("baseUri", "http://localhost");
        API_CONFIG.put("port", 9999);
        API_CONFIG.put("contentType", "application/json");
    }

    private static final Faker faker = new Faker();

    private DataGenerator() {}

    public interface UserAccount {
        String getLogin();
        String getPassword();
        String getStatus();
    }

    private static class Account implements UserAccount {
        private final String login;
        private final String password;
        private final String status;

        Account(String login, String password, String status) {
            this.login = login;
            this.password = password;
            this.status = status;
        }

        @Override
        public String getLogin() { return login; }

        @Override
        public String getPassword() { return password; }

        @Override
        public String getStatus() { return status; }
    }

    public static String generateRandomLogin() {
        return faker.name().username().replaceAll("\\.", "_") +
                faker.random().nextInt(100, 999);
    }

    public static String generateRandomPassword() {
        return faker.internet().password(8, 16, true, true);
    }

    public static UserAccount createUser(String status) {
        return new Account(
                generateRandomLogin(),
                generateRandomPassword(),
                status
        );
    }

    public static UserAccount registerUser(String status) {
        UserAccount user = createUser(status);
        register(user);
        return user;
    }

    private static void register(UserAccount user) {
        RequestSpecification request = RestAssured.given()
                .baseUri(API_CONFIG.get("baseUri").toString())
                .port(Integer.parseInt(API_CONFIG.get("port").toString()))
                .contentType(API_CONFIG.get("contentType").toString())
                .accept(API_CONFIG.get("contentType").toString());

        Map<String, Object> userData = new HashMap<>();
        userData.put("login", user.getLogin());
        userData.put("password", user.getPassword());
        userData.put("status", user.getStatus());

        request.body(userData)
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }
}
