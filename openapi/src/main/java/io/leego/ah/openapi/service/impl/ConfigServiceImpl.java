package io.leego.ah.openapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.ah.openapi.dto.ConfigQueryDTO;
import io.leego.ah.openapi.entity.Config;
import io.leego.ah.openapi.repository.ConfigRepository;
import io.leego.ah.openapi.service.ConfigService;
import io.leego.ah.openapi.vo.ConfigVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
@Service
public class ConfigServiceImpl extends BaseServiceImpl implements ConfigService {
    private final ConfigRepository configRepository;

    public ConfigServiceImpl(ObjectMapper objectMapper, ConfigRepository configRepository) {
        super(objectMapper);
        this.configRepository = configRepository;
    }

    @Override
    public Map<String, String> listConfigs(ConfigQueryDTO dto) {
        List<Config> configs = (List<Config>) configRepository.findAll(dto.toPredicate());
        return configs.stream().collect(Collectors.toMap(Config::getKey, Config::getValue));
    }

    @Override
    public ConfigVO getConfig(String key) {
        return configRepository.findByKey(key)
                .map(o -> new ConfigVO(o.getId(), o.getGroup(), o.getKey(), o.getValue(), o.getVersion()))
                .orElse(null);
    }

    @Override
    public Integer getConfigVersion(String key) {
        return configRepository.findVersionByKey(key);
    }

}
