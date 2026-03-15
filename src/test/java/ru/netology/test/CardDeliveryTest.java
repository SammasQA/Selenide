package ru.netology.test;

import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeAll
    static void setUpAll() {

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        if (Boolean.parseBoolean(System.getProperty("selenide.headless", "false"))) {
            options.addArguments("--headless");
        }

        if (Boolean.parseBoolean(System.getProperty("selenide.headless", "false"))) {
            options.addArguments("--headless");
        }

        Configuration.browserCapabilities = options;
        // Configuration.timeout = 15000; //
    }

    @BeforeEach
    void setUp() {

        // Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
    }

    @Test
    void shouldSuccessfullySubmitFormWithComplexElements() {
        // --- Город с выбором из выпадающего списка ---
        $("[data-test-id=city] input").setValue("Мо");
        $(".input__menu").should(appear, Duration.ofSeconds(10));
        $$(".menu-item").findBy(text("Москва")).click();

        // --- Дата через календарь на неделю вперёд ---
        $("[data-test-id=date] button").click();
        $(".calendar__day").should(appear, Duration.ofSeconds(5));

        LocalDate targetDate = LocalDate.now().plusDays(7);
        int targetDay = targetDate.getDayOfMonth();
        $$(".calendar__day").findBy(text(String.valueOf(targetDay))).click();

        String expectedDate = targetDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=date] input").shouldHave(value(expectedDate));

        // --- Остальные поля ---
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79111111111");
        $("[data-test-id=agreement]").click();

        // Отправка формы
        $$("button").find(exactText("Забронировать")).shouldBe(enabled).click();

        // Проверка уведомления
        $("[data-test-id=notification]").should(appear, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(text("Встреча успешно забронирована"));
    }
}