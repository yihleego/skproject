package io.leego.ah.openapi.controller;

import io.leego.ah.openapi.dto.DataSyncDTO;
import io.leego.ah.openapi.service.DataSyncService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("sync")
public class DataSyncController {
    private final DataSyncService dataSyncService;

    public DataSyncController(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sync(@RequestBody DataSyncDTO dto) {
        dataSyncService.sync(dto);
    }
}
