package io.leego.ah.openapi.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Function;

/**
 * @author Leego Yih
 */
public class Option<K, V> implements Serializable {
    @Serial
    private static final long serialVersionUID = -121222596239767866L;
    private final K key;
    private final V value;

    public Option(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public <X, Y> Option<X, Y> map(Function<? super K, ? extends X> keyMapper, Function<? super V, ? extends Y> valueMapper) {
        return new Option<>(keyMapper.apply(key), valueMapper.apply(value));
    }
}
