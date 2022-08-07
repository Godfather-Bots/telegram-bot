package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PictureGroupAttachment extends Attachment {

    private List<Picture> pictures;

    public void setPictureSizes(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public List<Picture> getPictureSizes() {
        return pictures;
    }

    public Optional<Picture> getLargePicture() {
        return pictures.stream()
                .max(Comparator.comparingInt(Picture::getFileSize));
    }

    @Override
    public String getType() {
        return TelegramAttachmentType.PICTURE.name();
    }

}
