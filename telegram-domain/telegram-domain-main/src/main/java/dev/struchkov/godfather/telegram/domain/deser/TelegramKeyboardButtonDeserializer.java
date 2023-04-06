package dev.struchkov.godfather.telegram.domain.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.struchkov.godfather.main.domain.keyboard.KeyBoardButton;
import dev.struchkov.godfather.telegram.domain.keyboard.button.ContactButton;
import dev.struchkov.godfather.telegram.domain.keyboard.button.SimpleButton;
import dev.struchkov.godfather.telegram.domain.keyboard.button.UrlButton;
import dev.struchkov.godfather.telegram.domain.keyboard.button.WebAppButton;

import java.io.IOException;

public class TelegramKeyboardButtonDeserializer extends StdDeserializer<KeyBoardButton> {

    public TelegramKeyboardButtonDeserializer() {
        this(null);
    }

    public TelegramKeyboardButtonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public KeyBoardButton deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final String typeKeyBoard = node.get("type").asText();
        switch (typeKeyBoard) {
            case "SIMPLE" -> {
                return parser.getCodec().treeToValue(node, SimpleButton.class);
            }
            case "CONTACT" -> {
                return parser.getCodec().treeToValue(node, ContactButton.class);
            }
            case "URL" -> {
                return parser.getCodec().treeToValue(node, UrlButton.class);
            }
            case "WEB_APP" -> {
                return parser.getCodec().treeToValue(node, WebAppButton.class);
            }
        }
        throw new RuntimeException();
    }

}
