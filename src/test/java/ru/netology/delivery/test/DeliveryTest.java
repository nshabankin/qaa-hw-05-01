package ru.netology.delivery.test;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;

import com.epam.reportportal.junit5.ReportPortalExtension;
import com.epam.reportportal.selenide.ReportPortalSelenideEventListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.openqa.selenium.Keys;

import ru.netology.delivery.data.DataGenerator;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class DeliveryTest {

    // Objects of testing elements with css-selectors
    SelenideElement cityField = $("[data-test-id='city'] input");
    SelenideElement dateField = $("[data-test-id='date'] input");
    SelenideElement nameField = $("[data-test-id='name'] input");
    SelenideElement phoneField = $("[data-test-id='phone'] input");
    SelenideElement agreementCheckbox = $("[data-test-id='agreement']");
    SelenideElement submitButton = $(".button");
    SelenideElement successNotification = $("[data-test-id='success-notification']");
    SelenideElement replanNotification = $("[data-test-id='replan-notification']");
    SelenideElement replanButton = $("[data-test-id='replan-notification'] .button");

    private static final Logger LOGGER = LogManager.getLogger(DeliveryTest.class);

    @BeforeEach
    @ExtendWith(ReportPortalExtension.class) // Each test reports to ReportPortal
    void setup() {
        open("http://localhost:9999");
        SelenideLogger.addListener("Report Portal logger", new ReportPortalSelenideEventListener());
        LOGGER.info("Hello from my simple test");
    }

    @Test
    @DisplayName("Should successfully plan and replan meeting")
    void shouldSuccessfullyPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        // First date request
        cityField.setValue(validUser.getCity());
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateField.setValue(firstMeetingDate);
        nameField.setValue(validUser.getName());
        phoneField.setValue(validUser.getPhone());
        agreementCheckbox.$(".checkbox__box").click();
        agreementCheckbox.$(".checkbox__control").shouldBe(selected);
        submitButton.click();
        successNotification.shouldBe(visible).shouldHave(text(firstMeetingDate));

        // Second date request
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateField.setValue(secondMeetingDate);
        submitButton.click();
        replanNotification.shouldBe(visible);
        replanButton.click();
        successNotification.shouldBe(visible).shouldHave(text(secondMeetingDate));
    }

    @Test
    @DisplayName("Should not plan if invalid city")
    void shouldNotPlanIfInvalidCity() {
        var invalidCityUser = DataGenerator.Registration.generateInvalidCityUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);

        // First date request
        cityField.setValue(invalidCityUser.getCity());
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateField.setValue(firstMeetingDate);
        nameField.setValue(invalidCityUser.getName());
        phoneField.setValue(invalidCityUser.getPhone());
        agreementCheckbox.$(".checkbox__box").click();
        agreementCheckbox.$(".checkbox__control").shouldBe(selected);
        submitButton.click();
        $("[data-test-id='city']").shouldHave(cssClass("input_invalid"));
        $("[data-test-id='city'] .input__sub").shouldBe(visible).shouldHave(text(("Доставка в выбранный город недоступна")));
    }

    @Test
    @DisplayName("Should plan if name contains specific symbols")
    void shouldPlanIfNameContainsSpecificSymbols() {
        var invalidNameUser = DataGenerator.Registration.generateInvalidNameUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        // First date request
        cityField.setValue(invalidNameUser.getCity());
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateField.setValue(firstMeetingDate);
        nameField.setValue(invalidNameUser.getName());
        phoneField.setValue(invalidNameUser.getPhone());
        agreementCheckbox.$(".checkbox__box").click();
        agreementCheckbox.$(".checkbox__control").shouldBe(selected);
        submitButton.click();
        successNotification.shouldBe(visible).shouldHave(text(firstMeetingDate));

        // Second date request
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        dateField.setValue(secondMeetingDate);
        submitButton.click();
        replanNotification.shouldBe(visible);
        replanButton.click();
        successNotification.shouldBe(visible).shouldHave(text(secondMeetingDate));
    }
}