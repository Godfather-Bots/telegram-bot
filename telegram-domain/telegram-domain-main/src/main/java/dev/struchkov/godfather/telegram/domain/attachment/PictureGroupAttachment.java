package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class PictureGroupAttachment extends Attachment {

    private List<Picture> pictures;

    public PictureGroupAttachment() {
        super(TelegramAttachmentType.PICTURE_GROUP.name());
    }

    public Optional<Picture> getLargePicture() {
        return pictures.stream()
                .max(Comparator.comparing(Picture::getFileSize));
    }

}
