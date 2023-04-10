package io.leego.ah.openapi.service;

import io.leego.ah.openapi.dto.ConfigQueryDTO;
import io.leego.ah.openapi.vo.ConfigVO;

import java.util.Map;

/**
 * @author Leego Yih
 */
public interface ConfigService {

    Map<String, String> listConfigs(ConfigQueryDTO dto);

    ConfigVO getConfig(String key);

    Integer getConfigVersion(String key);

}
