package io.leego.ah.openapi.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum TimeLeft implements CodeEnum<String, String> {
    /** Very Short: less than 5 minutes (time is extended with every new bid) */
    VERY_SHORT("VERY_SHORT", "Very Short", true, Duration.ofMinutes(5)),
    /** Short: 5 minutes - 1 hour */
    SHORT("SHORT", "Short", true, Duration.ofHours(1)),
    /** Medium: 1 - 4 hours */
    MEDIUM("MEDIUM", "Medium", true, Duration.ofHours(4)),
    /** Long: 4 - 24 hours */
    LONG("LONG", "Long", true, Duration.ofDays(1)),
    /** Very Long: 24+ hours */
    VERY_LONG("VERY_LONG", "Very Long", true, Duration.ofDays(2), Duration.ofDays(4)),
    ENDED("ENDED", "Ended", true, Duration.ZERO, Duration.ZERO),
    CANCELLED("CANCELLED", "Cancelled", false, Duration.ZERO, Duration.ZERO),
    ;
    private final String code;
    private final String name;
    private final boolean visible;
    private final Duration duration;
    private final Duration featuredDuration;

    TimeLeft(String code, String name, boolean visible, Duration duration) {
        this(code, name, visible, duration, duration);
    }

    public Duration getDuration(boolean featured) {
        return featured && featuredDuration != null ? featuredDuration : duration;
    }
}
