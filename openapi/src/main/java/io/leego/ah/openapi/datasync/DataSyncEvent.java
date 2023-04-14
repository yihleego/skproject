package io.leego.ah.openapi.datasync;

import org.springframework.context.ApplicationEvent;

import java.util.Collection;


/**
 * @author Leego Yih
 */
public class DataSyncEvent extends ApplicationEvent {
    private final DataSyncEventType type;
    private final String tag;

    public DataSyncEvent(Collection<?> source, DataSyncEventType type, String tag) {
        super(source);
        this.type = type;
        this.tag = tag;
    }

    public static DataSyncEvent create(Collection<?> source, String tag) {
        return new DataSyncEvent(source, DataSyncEventType.CREATE, tag);
    }

    public static DataSyncEvent update(Collection<?> source, String tag) {
        return new DataSyncEvent(source, DataSyncEventType.UPDATE, tag);
    }

    public static DataSyncEvent delete(Collection<?> source, String tag) {
        return new DataSyncEvent(source, DataSyncEventType.DELETE, tag);
    }

    public DataSyncEventType getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public Collection<?> getSource() {
        if (source != null && source instanceof Collection<?> entities && !entities.isEmpty()) {
            return entities;
        }
        return null;
    }
}
