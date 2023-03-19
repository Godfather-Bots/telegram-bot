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
    private String rootUrl;

    @ToString.Exclude
    private String path;

    @ToString.Exclude
    private String accessKey;

}
