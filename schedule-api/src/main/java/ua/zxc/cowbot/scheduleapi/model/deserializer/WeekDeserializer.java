package ua.zxc.cowbot.scheduleapi.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import ua.zxc.cowbot.scheduleapi.model.Day;
import ua.zxc.cowbot.scheduleapi.model.Week;

import java.io.IOException;
import java.util.List;

public class WeekDeserializer extends JsonDeserializer<Week> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final CollectionType COLLECTION_TYPE =
            TypeFactory
                    .defaultInstance()
                    .constructCollectionType(List.class, Day.class);

    @Override
    public Week deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ArrayNode objectNode = OBJECT_MAPPER.readTree(jsonParser);

        if (null == objectNode
                || !objectNode.isArray()
                || !objectNode.elements().hasNext())
            return null;

        return new Week(OBJECT_MAPPER.reader(COLLECTION_TYPE).readValue(objectNode));
    }
}
