package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeEach
    void setUp() {
        // Открываем страницу с формой
        open("http://localhost:9999");
        Configuration.holdBrowserOpen = false; // можно оставить false для CI
    }

    @Test
    void shouldSuccessfullySubmitForm() {
        // Генерируем дату: текущая + 3 дня
        LocalDate deliveryDate = LocalDate.now().plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String plannedDate = deliveryDate.format(formatter);

        // Ввод города (например, "Москва")
        $("[data-test-id=city] input").setValue("Москва");

        // Очистка поля даты (если там уже есть значение)
        $("[data-test-id=date] input").doubleClick(); // выделяем текст двойным кликом
        $("[data-test-id=date] input").sendKeys(Keys.BACK_SPACE); // удаляем выделенное
        $("[data-test-id=date] input").setValue(plannedDate);

        // Ввод имени
        $("[data-test-id=name] input").setValue("Иванов-Петров Иван");

        // Ввод телефона
        $("[data-test-id=phone] input").setValue("+71234567890");

        // Отметка чекбокса согласия
        $("[data-test-id=agreement]").click();

        // Нажатие на кнопку "Забронировать"
        $("[data-test-id=button] button").click();

        // Ожидаем появления спиннера загрузки и его исчезновения (не более 15 секунд)
        $("[data-test-id=loading]").shouldBe(Condition.visible, Duration.ofSeconds(5)); // спиннер появился
        $("[data-test-id=loading]").shouldBe(Condition.hidden, Duration.ofSeconds(15)); // исчез

        // Проверяем появление всплывающего окна с сообщением об успехе
        $(byText("Успешно!")).shouldBe(Condition.visible, Duration.ofSeconds(5));
        // Или более точный селектор, если есть data-test-id у окна
        // $("[data-test-id=success-notification]").shouldBe(Condition.visible);
    }
}