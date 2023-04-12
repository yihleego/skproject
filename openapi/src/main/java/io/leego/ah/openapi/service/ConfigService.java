package io.leego.ah.openapi.service;

import io.leego.ah.openapi.dto.ConfigQueryDTO;
import io.leego.ah.openapi.vo.ConfigVO;

import java.util.List;

/**
 * @author Leego Yih
 */
public interface ConfigService {

    List<ConfigVO> listConfigs(ConfigQueryDTO dto);

    ConfigVO getConfig(String group, String key);

    Integer getConfigVersion(String group, String key);

}
