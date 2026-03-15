package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
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
        // Автоматическая загрузка подходящего chromedriver
        WebDriverManager.chromedriver().setup();

    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        Configuration.timeout = 15000;
        Configuration.holdBrowserOpen = false; // для ci false
    }

    @Test
    void shouldSuccessfullySubmitForm() {
        LocalDate deliveryDate = LocalDate.now().plusDays(7);
        String formattedDate = deliveryDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(formattedDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79111111111");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        $("[data-test-id=notification]").should(appear, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(text("Встреча успешно забронирована"));
    }
}