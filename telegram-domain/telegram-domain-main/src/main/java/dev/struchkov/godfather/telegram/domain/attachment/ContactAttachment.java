package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.main.domain.content.Attachment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactAttachment extends Attachment {

    private String phoneNumber;
    private String firstName;
    private String lastName;
    private Long userId;
    private String vCard;

    /**
     * если true, то контакт принадлежит отправившему телеграм аккаунту.
     */
    private boolean owner;

    public ContactAttachment() {
        super(TelegramAttachmentType.CONTACT.name());
    }

}
