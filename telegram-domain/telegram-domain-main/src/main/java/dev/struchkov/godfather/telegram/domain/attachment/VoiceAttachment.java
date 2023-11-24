package dev.struchkov.godfather.telegram.domain.attachment;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class VoiceAttachment extends FileAttachment {

    private Duration duration;

    public VoiceAttachment() {
        super(TelegramAttachmentType.VOICE.name());
    }

}
