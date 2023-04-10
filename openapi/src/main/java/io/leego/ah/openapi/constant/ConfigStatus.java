package io.leego.ah.openapi.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum ConfigStatus {
    DISABLED(0),
    ENABLED(1),
    ;
    private final int code;
}
