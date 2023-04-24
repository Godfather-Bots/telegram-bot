package dev.struchkov.godfather.telegram.quarkus.domain.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.struchkov.godfather.quarkus.domain.content.send.SendAttachment;
import dev.struchkov.godfather.telegram.quarkus.domain.attachment.send.DocumentSendAttachment;
import dev.struchkov.godfather.telegram.quarkus.domain.attachment.send.PhotoSendAttachment;

import java.io.IOException;

public class TelegramAttachmentSendDeserializer extends StdDeserializer<SendAttachment> {

    public TelegramAttachmentSendDeserializer() {
        this(null);
    }

    public TelegramAttachmentSendDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SendAttachment deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final String attachmentType = node.get("type").asText();
        return switch (attachmentType) {
            case "PHOTO" -> parser.getCodec().treeToValue(node, PhotoSendAttachment.class);
            case "DOCUMENT" -> parser.getCodec().treeToValue(node, DocumentSendAttachment.class);
            default -> throw new IllegalStateException("Unexpected value: " + attachmentType);
        };
    }

}
