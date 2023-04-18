package io.leego.ah.openapi.service;

import io.leego.ah.openapi.dto.DataSyncDTO;

import java.util.Collection;

/**
 * @author Leego Yih
 */
public interface DataSyncService {

    void sync(DataSyncDTO dto);

    void create(Collection<?> entities, String tag);

    void update(Collection<?> entities, String tag);

    void delete(Collection<?> entities, String tag);

}
