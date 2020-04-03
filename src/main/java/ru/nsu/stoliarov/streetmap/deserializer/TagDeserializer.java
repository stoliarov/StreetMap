package ru.nsu.stoliarov.streetmap.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TagDeserializer extends StdDeserializer<List<Pair<String, String>>> {

    public TagDeserializer() {
        this(null);
    }

    public TagDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public List<Pair<String, String>> deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {

        JsonNode nodes = parser.getCodec().readTree(parser);

        if (nodes == null || nodes.getNodeType() != JsonNodeType.ARRAY) {
            return null;
        }

        List<Pair<String, String>> result = new ArrayList<>();

        for (JsonNode node : nodes) {

            if (node.getNodeType() != JsonNodeType.OBJECT) {
                continue;
            }

            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

            if (!fields.hasNext()) {
                continue;
            }

            Map.Entry<String, JsonNode> field = fields.next();

            String key = field.getKey();
            JsonNode value = field.getValue();

            result.add(Pair.of(key, value.asText()));
        }
        
        return result;
    }
}
