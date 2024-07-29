package ru.netology.delivery.test;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class DeliveryTest {

    // Объекты тестовых элементов с css-селекторами
    SelenideElement cityField = $("[data-test-id='city'] input");
    SelenideElement dateField = $("[data-test-id='date'] input");
    SelenideElement nameField = $("[data-test-id='name'] input");
    SelenideElement phoneField = $("[data-test-id='phone'] input");
    SelenideElement agreementCheckbox = $("[data-test-id='agreement']");
    SelenideElement submitButton = $(".button");
    SelenideElement successNotification = $("[data-test-id='success-notification']");
    SelenideElement replanNotification = $("[data-test-id='replan-notification']");
    SelenideElement replanButton = $("[data-test-id='replan-notification'] .button");

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully plan and replan meeting")
    void shouldSuccessfullyPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        // Заполнение заявки на первую дату
        cityField.setValue(validUser.getCity());
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateField.setValue(firstMeetingDate);
        nameField.setValue(validUser.getName());
        phoneField.setValue(validUser.getPhone());
        agreementCheckbox.$(".checkbox__box").click();
        agreementCheckbox.$(".checkbox__control").shouldBe(selected);
        submitButton.click();
        successNotification.shouldBe(visible).shouldHave(text(firstMeetingDate));

        // Заполнение заявки на вторую дату
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateField.setValue(secondMeetingDate);
        submitButton.click();
        replanNotification.shouldBe(visible);
        replanButton.click();
        successNotification.shouldBe(visible).shouldHave(text(secondMeetingDate));
    }

    @Test
    void shouldNotPlanIfInvalidCity() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);

        // Заполнение заявки на первую дату
        cityField.setValue("Новокузнецк");
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateField.setValue(firstMeetingDate);
        nameField.setValue(validUser.getName());
        phoneField.setValue(validUser.getPhone());
        agreementCheckbox.$(".checkbox__box").click();
        agreementCheckbox.$(".checkbox__control").shouldBe(selected);
        submitButton.click();
        $("[data-test-id='city']").shouldHave(cssClass("input_invalid"));
        $("[data-test-id='city'] .input__sub").shouldBe(visible).shouldHave(text(("Доставка в выбранный город недоступна")));
    }

    @Test
    void shouldPlanIfNameContainsSpecificSymbols() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        // Заполнение заявки на первую дату
        cityField.setValue(validUser.getCity());
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateField.setValue(firstMeetingDate);
        nameField.setValue("Артём Грулёв");
        phoneField.setValue(validUser.getPhone());
        agreementCheckbox.$(".checkbox__box").click();
        agreementCheckbox.$(".checkbox__control").shouldBe(selected);
        submitButton.click();
        successNotification.shouldBe(visible).shouldHave(text(firstMeetingDate));

        // Заполнение заявки на вторую дату
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateField.setValue(secondMeetingDate);
        submitButton.click();
        replanNotification.shouldBe(visible);
        replanButton.click();
        successNotification.shouldBe(visible).shouldHave(text(secondMeetingDate));
    }
}