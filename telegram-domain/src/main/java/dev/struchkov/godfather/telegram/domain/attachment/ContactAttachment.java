package dev.struchkov.godfather.telegram.domain.attachment;

import dev.struchkov.godfather.context.domain.content.attachment.Attachment;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public String getVCard() {
        return vCard;
    }

    public void setVCard(String vCard) {
        this.vCard = vCard;
    }

    @Override
    public String getType() {
        return TelegramAttachmentType.CONTACT.name();
    }

}
