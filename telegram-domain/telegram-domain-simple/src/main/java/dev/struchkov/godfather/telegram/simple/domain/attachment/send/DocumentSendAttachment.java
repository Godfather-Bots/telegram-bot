package dev.struchkov.godfather.telegram.simple.domain.attachment.send;

import dev.struchkov.godfather.simple.domain.content.send.SendAttachment;
import dev.struchkov.godfather.simple.domain.content.send.SendFile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentSendAttachment implements SendAttachment {

    public static final String TYPE = "DOCUMENT";

    private SendFile sendFile;

    @Override
    public String getType() {
        return TYPE;
    }

}
