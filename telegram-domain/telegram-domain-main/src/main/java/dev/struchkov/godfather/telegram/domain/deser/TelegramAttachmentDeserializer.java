package dev.struchkov.godfather.telegram.domain.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.struchkov.godfather.main.domain.content.Attachment;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.CommandAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.ContactAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.DocumentAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.LinkAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.PictureGroupAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.StickerAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.TelegramAttachmentType;
import dev.struchkov.godfather.telegram.domain.attachment.VideoAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.VoiceAttachment;
import dev.struchkov.haiti.utils.ObjectUtils;

import java.io.IOException;

public class TelegramAttachmentDeserializer extends StdDeserializer<Attachment> {

    public TelegramAttachmentDeserializer() {
        this(null);
    }

    public TelegramAttachmentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Attachment deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final String typeAttachmentString = node.get("type").asText();
        final TelegramAttachmentType type = ObjectUtils.createEnum(typeAttachmentString, TelegramAttachmentType.class)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип вложения: " + typeAttachmentString));
        return switch (type) {
            case BUTTON_CLICK -> parser.getCodec().treeToValue(node, ButtonClickAttachment.class);
            case DOCUMENT -> parser.getCodec().treeToValue(node, DocumentAttachment.class);
            case CONTACT -> parser.getCodec().treeToValue(node, ContactAttachment.class);
            case PICTURE_GROUP -> parser.getCodec().treeToValue(node, PictureGroupAttachment.class);
            case LINK -> parser.getCodec().treeToValue(node, LinkAttachment.class);
            case COMMAND -> parser.getCodec().treeToValue(node, CommandAttachment.class);
            case VIDEO -> parser.getCodec().treeToValue(node, VideoAttachment.class);
            case VOICE -> parser.getCodec().treeToValue(node, VoiceAttachment.class);
            case STICKER -> parser.getCodec().treeToValue(node, StickerAttachment.class);
            case PICTURE -> null;
        };
    }

}
