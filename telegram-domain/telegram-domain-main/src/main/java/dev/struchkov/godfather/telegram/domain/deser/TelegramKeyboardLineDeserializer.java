package dev.struchkov.godfather.telegram.domain.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.struchkov.godfather.main.domain.keyboard.KeyBoardLine;
import dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine;

import java.io.IOException;

public class TelegramKeyboardLineDeserializer extends StdDeserializer<KeyBoardLine> {

    public TelegramKeyboardLineDeserializer() {
        this(null);
    }

    public TelegramKeyboardLineDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public KeyBoardLine deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return parser.getCodec().treeToValue(node, SimpleKeyBoardLine.class);
    }

}
