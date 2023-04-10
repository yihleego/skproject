package io.leego.ah.openapi.controller;

import io.leego.ah.openapi.annotation.Privileged;
import io.leego.ah.openapi.constant.BidStatus;
import io.leego.ah.openapi.constant.CodeEnum;
import io.leego.ah.openapi.constant.ItemGroup;
import io.leego.ah.openapi.constant.ItemLocation;
import io.leego.ah.openapi.constant.TimeLeft;
import io.leego.ah.openapi.constant.VariantName;
import io.leego.ah.openapi.constant.VariantValue;
import io.leego.ah.openapi.dto.ConfigQueryDTO;
import io.leego.ah.openapi.service.ConfigService;
import io.leego.ah.openapi.util.Option;
import io.leego.ah.openapi.vo.ConfigVO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping
public class BasicController {
    public static final Map<String, List<Option<String, String>>> ENUMS;

    static {
        ENUMS = Map.of(
                BidStatus.class.getSimpleName(), CodeEnum.toOptions(BidStatus.values()),
                TimeLeft.class.getSimpleName(), CodeEnum.toOptions(TimeLeft.values()),
                ItemGroup.class.getSimpleName(), CodeEnum.toOptions(ItemGroup.values()),
                ItemLocation.class.getSimpleName(), CodeEnum.toOptions(ItemLocation.values()),
                VariantName.class.getSimpleName(), CodeEnum.toOptions(VariantName.values()),
                VariantValue.class.getSimpleName(), CodeEnum.toOptions(VariantValue.values())
        );
    }

    private final ConfigService configService;

    public BasicController(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Returns all enums.
     *
     * @return all enums.
     */
    @Privileged
    @GetMapping("enums")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, List<Option<String, String>>> listAllEnums() {
        return ENUMS;
    }

    /**
     * Returns the configs with the given query criteria.
     *
     * @param dto the query criteria.
     * @return the configs.
     */
    @GetMapping("configs")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> listConfigs(@Validated ConfigQueryDTO dto) {
        return configService.listConfigs(dto);
    }

    /**
     * Returns the config with the given key.
     *
     * @param key the config key.
     * @return the config.
     */
    @GetMapping("configs/{key}")
    @ResponseStatus(HttpStatus.OK)
    public ConfigVO getConfig(@PathVariable String key) {
        return configService.getConfig(key);
    }

    /**
     * Returns the config version with the given key.
     *
     * @param key the config key.
     * @return the config version.
     */
    @GetMapping("configs/{key}/version")
    @ResponseStatus(HttpStatus.OK)
    public Integer getConfigVersion(@PathVariable String key) {
        return configService.getConfigVersion(key);
    }

}
