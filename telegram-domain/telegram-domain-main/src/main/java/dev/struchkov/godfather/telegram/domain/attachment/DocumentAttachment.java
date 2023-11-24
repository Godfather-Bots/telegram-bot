package dev.struchkov.godfather.telegram.domain.attachment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentAttachment extends FileAttachment {

    public DocumentAttachment() {
        super(TelegramAttachmentType.DOCUMENT.name());
    }

}
