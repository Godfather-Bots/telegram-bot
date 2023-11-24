package dev.struchkov.godfather.telegram.domain.attachment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StickerAttachment extends FileAttachment {

    private String emoji;
    private boolean video;
    private boolean animated;

    public StickerAttachment() {
        super(TelegramAttachmentType.STICKER.name());
    }

}
