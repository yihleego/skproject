package io.leego.ah.openapi.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.leego.ah.openapi.util.Sort;

import java.io.IOException;

/**
 * @author Leego Yih
 */
public class SortSerializer extends JsonSerializer<Sort> {
    @Override
    public void serialize(Sort value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value != null) {
            jsonGenerator.writeString(Sort.format(value));
        }
    }
}
