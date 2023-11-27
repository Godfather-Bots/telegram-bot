package dev.struchkov.godfather.telegram.domain.attachment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoAttachment extends FileAttachment {

    public VideoAttachment() {
        super(TelegramAttachmentType.VIDEO.name());
    }

}
