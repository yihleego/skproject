package io.leego.ah.openapi.event;

import org.springframework.context.ApplicationEvent;


/**
 * @author Leego Yih
 */
public class DataSyncEvent extends ApplicationEvent {
    private final DataSyncEventType type;
    private final String tag;

    public DataSyncEvent(Object source, DataSyncEventType type, String tag) {
        super(source);
        this.type = type;
        this.tag = tag;
    }

    public DataSyncEventType getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    public static DataSyncEvent insert(Object source, String tag) {
        return new DataSyncEvent(source, DataSyncEventType.INSERT, tag);
    }

    public static DataSyncEvent update(Object source, String tag) {
        return new DataSyncEvent(source, DataSyncEventType.UPDATE, tag);
    }

    public static DataSyncEvent delete(Object source, String tag) {
        return new DataSyncEvent(source, DataSyncEventType.DELETE, tag);
    }
}
