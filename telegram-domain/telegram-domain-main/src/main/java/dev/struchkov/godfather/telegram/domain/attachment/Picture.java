package dev.struchkov.godfather.telegram.domain.attachment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Picture extends FileAttachment {

    private Integer weight;
    private Integer height;

    public Picture() {
        super(TelegramAttachmentType.PICTURE.name());
    }

}
