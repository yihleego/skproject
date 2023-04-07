package io.leego.ah.openapi.constant;

import io.leego.ah.openapi.util.Option;

import java.util.Arrays;
import java.util.List;

public interface CodeEnum<C, N> {

    C getCode();

    N getName();

    default boolean isVisible() {
        return true;
    }

    static <K, V> List<Option<K, V>> toOptions(CodeEnum<K, V>[] es) {
        return Arrays.stream(es)
                .filter(CodeEnum::isVisible)
                .map(t -> new Option<>(t.getCode(), t.getName()))
                .toList();
    }
}
