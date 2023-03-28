package dev.struchkov.godfather.telegram.domain.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.struchkov.godfather.main.domain.keyboard.KeyBoard;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import dev.struchkov.godfather.telegram.domain.keyboard.MarkupKeyBoard;

import java.io.IOException;

public class TelegramKeyboardDeserializer extends StdDeserializer<KeyBoard> {

    public TelegramKeyboardDeserializer() {
        this(null);
    }

    public TelegramKeyboardDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public KeyBoard deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final String typeKeyBoard = node.get("type").asText();
        switch (typeKeyBoard) {
            case "INLINE" -> {
                return parser.getCodec().treeToValue(node, InlineKeyBoard.class);
            }
            case "MARKUP" -> {
                return parser.getCodec().treeToValue(node, MarkupKeyBoard.class);
            }
        }
        throw new RuntimeException();
    }

}
