package io.leego.ah.openapi.service.impl;

import io.leego.ah.openapi.pojo.dto.ConfigQueryDTO;
import io.leego.ah.openapi.pojo.vo.ConfigVO;
import io.leego.ah.openapi.repository.ConfigRepository;
import io.leego.ah.openapi.service.ConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

/**
 * @author Leego Yih
 */
@Service
public class ConfigServiceImpl implements ConfigService {
    private final ConfigRepository configRepository;

    public ConfigServiceImpl(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public List<ConfigVO> listConfigs(ConfigQueryDTO dto) {
        return configRepository.findAll(dto.toPredicate()).stream().map(ConfigVO::from).toList();
    }

    @Override
    public List<ConfigVO> listConfigs(String group) {
        return configRepository.findByGroup(group).stream().map(ConfigVO::from).toList();
    }

    @Override
    public ConfigVO getConfig(String group, String key) {
        return configRepository.findByGroupAndKey(group, key).map(ConfigVO::from)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    @Override
    public Integer getConfigVersion(String group, String key) {
        return configRepository.findVersionByGroupAndKey(group, key)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }
}
