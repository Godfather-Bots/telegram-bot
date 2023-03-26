package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentAttachment extends Attachment {

    private String fileId;
    private Long fileSize;
    private String fileName;
    private String mimeType;

    public DocumentAttachment() {
        super(TelegramAttachmentType.DOCUMENT.name());
    }

}
