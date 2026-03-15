package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    // Задача №1
    @Test
    @DisplayName("Должен успешно планировать встречу при прямом вводе города и даты")
    void shouldPlanMeetingWithDirectInput() {
        LocalDate meetingDate = LocalDate.now().plusDays(3);
        String formattedDate = meetingDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(formattedDate).pressTab();
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79111111111");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(text("Встреча успешно забронирована"), text(formattedDate));
    }

    // Задача №2
    @Test
    @DisplayName("Должен планировать встречу с выбором города из списка и даты из календаря на следующий месяц")
    void shouldPlanMeetingWithComplexElementsAndMonthSwitch() {
        // Город с выбором из выпадающего списка
        $("[data-test-id=city] input").setValue("Мо");
        $(".input__menu").should(appear, Duration.ofSeconds(5));
        $$(".menu-item").findBy(text("Москва")).click();

        // Дата через календарь на 4 недели вперёд (гарантированно следующий месяц)
        LocalDate targetDate = LocalDate.now().plusWeeks(4);
        selectDateInCalendar(targetDate);

        // Остальные поля
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79111111111");
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Забронировать")).click();

        // Проверка уведомления с правильной датой
        String expectedDate = targetDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(text("Встреча успешно забронирована"), text(expectedDate));
    }

    private void selectDateInCalendar(LocalDate targetDate) {
        // Открыть календарь
        $("[data-test-id=date] button").click();
        $(".calendar").should(appear, Duration.ofSeconds(5));

        YearMonth targetYearMonth = YearMonth.from(targetDate);
        System.out.println("Целевой месяц/год: " + targetYearMonth);


        int arrowsCount = $$(".calendar__arrow_direction_right").size();
        System.out.println("Найдено стрелок с классом .calendar__arrow_direction_right: " + arrowsCount);

        for (int i = 0; i < 12; i++) {
            String headerText = $(".calendar__name").shouldBe(visible).getText().trim();
            YearMonth currentYearMonth = parseHeaderToYearMonth(headerText);
            System.out.println("Текущий месяц/год: " + currentYearMonth);

            if (currentYearMonth.equals(targetYearMonth)) {
                $$(".calendar__day").findBy(text(String.valueOf(targetDate.getDayOfMonth()))).click();
                return;
            }


            if (arrowsCount > 1) {

                $$(".calendar__arrow_direction_right").get(1).click();
            } else {

                $(".calendar__arrow_direction_right").click();
            }

            // Ждём изменения заголовка
            $(".calendar__name").shouldNotHave(text(headerText), Duration.ofSeconds(3));
        }

        throw new AssertionError("Не удалось выбрать дату " + targetDate + " в календаре");
    }


    private YearMonth parseHeaderToYearMonth(String header) {
        String[] parts = header.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Неверный формат заголовка: " + header);
        }
        String monthName = parts[0].toLowerCase();
        int year = Integer.parseInt(parts[1]);

        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("январь", 1);
        monthMap.put("февраль", 2);
        monthMap.put("март", 3);
        monthMap.put("апрель", 4);
        monthMap.put("май", 5);
        monthMap.put("июнь", 6);
        monthMap.put("июль", 7);
        monthMap.put("август", 8);
        monthMap.put("сентябрь", 9);
        monthMap.put("октябрь", 10);
        monthMap.put("ноябрь", 11);
        monthMap.put("декабрь", 12);

        Integer month = monthMap.get(monthName);
        if (month == null) {
            throw new IllegalArgumentException("Неизвестный месяц: " + monthName);
        }
        return YearMonth.of(year, month);
    }
}