package dev.struchkov.godfather.telegram.domain.event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Subscribe {

    private String telegramId;
    private String login;
    private LocalDateTime subscriptionDate;
    private String languageCode;
    private boolean premium;
    private String firstName;
    private String lastName;

}
