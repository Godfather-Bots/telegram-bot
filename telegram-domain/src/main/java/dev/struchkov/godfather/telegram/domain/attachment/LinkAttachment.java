package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;
import dev.struchkov.haiti.utils.Parser;
import dev.struchkov.haiti.utils.domain.CompositeUrl;

public class LinkAttachment extends Attachment {

    private String url;

    public LinkAttachment(String url) {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getType() {
        return TelegramAttachmentType.LINK.name();
    }

    public CompositeUrl split() {
        return Parser.url(url);
    }

}
