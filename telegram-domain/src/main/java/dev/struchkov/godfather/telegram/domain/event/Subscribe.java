package dev.struchkov.godfather.telegram.domain.event;

import dev.struchkov.godfather.main.domain.event.Event;

import java.time.LocalDateTime;

public class Subscribe implements Event {

    public static final String TYPE = "SUBSCRIBE";

    private String telegramId;
    private String firstName;
    private String lastName;
    private LocalDateTime subscriptionDate;

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(LocalDateTime subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    @Override
    public String getEventType() {
        return TYPE;
    }

}
