package dev.struchkov.godfather.telegram.domain.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookConfig {

    private boolean enable = false;
    private String rootUrl;
    private String path;
    private String accessKey;

}
