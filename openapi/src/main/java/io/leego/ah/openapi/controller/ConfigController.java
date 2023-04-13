package io.leego.ah.openapi.controller;

import io.leego.ah.openapi.dto.ConfigQueryDTO;
import io.leego.ah.openapi.service.ConfigService;
import io.leego.ah.openapi.vo.ConfigVO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("configs")
public class ConfigController {
    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Returns the configs with the given query criteria.
     *
     * @param dto the query criteria.
     * @return the configs.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ConfigVO> listConfigs(@Validated ConfigQueryDTO dto) {
        return configService.listConfigs(dto);
    }

    /**
     * Returns the configs with the given group.
     *
     * @param group the config group.
     * @return the configs.
     */
    @GetMapping("{group}")
    @ResponseStatus(HttpStatus.OK)
    public List<ConfigVO> listConfigs(@PathVariable("group") String group) {
        return configService.listConfigs(group);
    }

    /**
     * Returns the config with the given group and key.
     *
     * @param group the config group.
     * @param key   the config key.
     * @return the config.
     * @throws HttpClientErrorException.NotFound if the config is not found.
     */
    @GetMapping("{group}/{key}")
    @ResponseStatus(HttpStatus.OK)
    public ConfigVO getConfig(@PathVariable("group") String group, @PathVariable("key") String key) {
        return configService.getConfig(group, key);
    }

    /**
     * Returns the config version with the given group and key.
     *
     * @param group the config group.
     * @param key   the config key.
     * @return the config version.
     * @throws HttpClientErrorException.NotFound if the config is not found.
     */
    @GetMapping("{group}/{key}/version")
    @ResponseStatus(HttpStatus.OK)
    public Integer getConfigVersion(@PathVariable("group") String group, @PathVariable("key") String key) {
        return configService.getConfigVersion(group, key);
    }

}
