package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.context.domain.content.attachment.Attachment;

public class DocumentAttachment extends Attachment {

    private String fileId;
    private Long fileSize;
    private String fileName;
    private String mimeType;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String getType() {
        return TelegramAttachmentType.DOCUMENT.name();
    }

}
