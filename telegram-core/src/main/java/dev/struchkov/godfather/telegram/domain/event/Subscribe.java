package dev.struchkov.godfather.telegram.domain.event;

import dev.struchkov.godfather.context.domain.event.Event;

import java.time.LocalDateTime;

public class Subscribe implements Event {

    public static final String TYPE = "SUBSCRIBE";

    private Long telegramId;
    private String firstName;
    private String lastName;
    private LocalDateTime subscriptionDate;

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
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
    public String getType() {
        return TYPE;
    }

}