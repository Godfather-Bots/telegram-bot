package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;
import dev.struchkov.haiti.utils.Parser;
import dev.struchkov.haiti.utils.container.CompositeUrl;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkAttachment extends Attachment {

    private String url;

    public LinkAttachment() {
        super(TelegramAttachmentType.LINK.name());
    }

    public LinkAttachment(String url) {
        super(TelegramAttachmentType.LINK.name());
        this.url = url;
    }

    public CompositeUrl split() {
        return Parser.url(url);
    }

}
