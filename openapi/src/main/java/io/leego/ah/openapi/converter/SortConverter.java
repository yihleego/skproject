package io.leego.ah.openapi.converter;

import io.leego.ah.openapi.util.Sort;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Leego Yih
 */
public class SortConverter implements Converter<String, Sort> {

    @Override
    public Sort convert(String source) {
        return source == null || source.isBlank() ? null : Sort.parse(source);
    }

}
