package io.leego.ah.openapi.util;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author Leego Yih
 */
public final class TrimUtils {

    public static String trim(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.strip();
    }

    public static String trim(String s, Function<String, String> mapper) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return mapper.apply(s.strip());
    }

    public static <C extends Collection<E>, E> C trim(C c) {
        if (c == null || c.isEmpty()) {
            return null;
        }
        return c;
    }

}
