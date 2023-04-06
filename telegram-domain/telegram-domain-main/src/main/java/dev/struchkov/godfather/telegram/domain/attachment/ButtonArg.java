package dev.struchkov.godfather.telegram.domain.attachment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ButtonArg {

    private String type;
    private String value;

    public static ButtonArg buttonArg(String type, String value) {
        return new ButtonArg(type, value);
    }

}
