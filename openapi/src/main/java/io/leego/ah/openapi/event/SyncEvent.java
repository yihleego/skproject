package io.leego.ah.openapi.event;

import org.springframework.context.ApplicationEvent;


/**
 * @author Leego Yih
 */
public class SyncEvent extends ApplicationEvent {
    private final EventType type;
    private final String tag;

    public SyncEvent(Object source, EventType type, String tag) {
        super(source);
        this.type = type;
        this.tag = tag;
    }

    public EventType getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }
}
