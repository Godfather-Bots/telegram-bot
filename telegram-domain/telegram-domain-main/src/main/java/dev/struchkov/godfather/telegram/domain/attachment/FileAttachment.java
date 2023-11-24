package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileAttachment extends Attachment {

    protected String fileId;
    protected Long fileSize;
    protected String mimeType;
    private String fileName;

    protected FileAttachment(String type) {
        super(type);
    }

}
