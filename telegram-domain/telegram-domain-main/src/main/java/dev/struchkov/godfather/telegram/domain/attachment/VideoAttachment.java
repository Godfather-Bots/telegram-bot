package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoAttachment extends Attachment {

    private String fileId;
    private Long fileSize;
    private String fileName;

    public VideoAttachment() {
        super(TelegramAttachmentType.VIDEO.name());
    }

}
