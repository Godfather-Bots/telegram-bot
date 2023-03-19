package dev.struchkov.godfather.telegram.domain.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WebhookConfig {

    private boolean enable = false;

    @ToString.Exclude
    private String url;

    @ToString.Exclude
    private String secretToken;

}
