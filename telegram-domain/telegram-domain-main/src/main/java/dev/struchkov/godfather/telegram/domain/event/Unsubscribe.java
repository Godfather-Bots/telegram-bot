package dev.struchkov.godfather.telegram.domain.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Unsubscribe {

    private String telegramId;
    private String firstName;
    private String lastName;
    private LocalDateTime subscriptionDate;

}
