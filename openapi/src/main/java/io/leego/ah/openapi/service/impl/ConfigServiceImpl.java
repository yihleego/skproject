package io.leego.ah.openapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.ah.openapi.dto.ConfigQueryDTO;
import io.leego.ah.openapi.entity.Config;
import io.leego.ah.openapi.repository.ConfigRepository;
import io.leego.ah.openapi.service.ConfigService;
import io.leego.ah.openapi.vo.ConfigVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

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
    public List<ConfigVO> listConfigs(ConfigQueryDTO dto) {
        List<Config> configs = (List<Config>) configRepository.findAll(dto.toPredicate());
        return configs.stream().map(this::toVO).toList();
    }

    @Override
    public List<ConfigVO> listConfigs(String group) {
        return configRepository.findByGroup(group).stream().map(this::toVO).toList();
    }

    @Override
    public ConfigVO getConfig(String group, String key) {
        return configRepository.findByGroupAndKey(group, key).map(this::toVO)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    @Override
    public Integer getConfigVersion(String group, String key) {
        return configRepository.findVersionByGroupAndKey(group, key)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    private ConfigVO toVO(Config config) {
        return new ConfigVO(
                config.getGroup(),
                config.getKey(),
                config.getValue(),
                config.getVersion());
    }
}
