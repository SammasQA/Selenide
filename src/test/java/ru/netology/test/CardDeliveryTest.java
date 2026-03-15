package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

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
       // Configuration.timeout = 15000; //для тестов
    }

    @BeforeEach
    void setUp() {
        Configuration.holdBrowserOpen = false; // для отладки
        // Открываем браузер только для отладки локально
        if (!"true".equals(System.getProperty("selenide.headless"))) {
            Configuration.holdBrowserOpen = true;
        }
        open("http://localhost:9999");
    }

    @Test
    void shouldSuccessfullySubmitFormWithComplexElements() {
        // --- Город с выбором из выпадающего списка ---
        $("[data-test-id=city] input").setValue("Мо");
        $(".input__menu").should(appear, Duration.ofSeconds(10));
        $$(".menu-item").findBy(text("Москва")).click();

        // --- Дата через календарь на неделю вперёд ---
        // Открываем календарь (кнопка с иконкой рядом с полем даты)
        $("[data-test-id=date] button").click();

        // Ждём появления календаря (любая ячейка с классом calendar__day)
        $(".calendar__day").should(appear, Duration.ofSeconds(5));

        // Вычисляем нужную дату: текущая + 7 дней
        LocalDate targetDate = LocalDate.now().plusDays(7);
        int targetDay = targetDate.getDayOfMonth();


        $$(".calendar__day").findBy(text(String.valueOf(targetDay))).click();

        // Проверяем, что поле даты заполнилось корректно
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