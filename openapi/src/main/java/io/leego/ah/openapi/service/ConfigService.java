package io.leego.ah.openapi.service;

import io.leego.ah.openapi.pojo.dto.ConfigQueryDTO;
import io.leego.ah.openapi.pojo.vo.ConfigVO;

import java.util.List;

/**
 * @author Leego Yih
 */
public interface ConfigService {

    List<ConfigVO> listConfigs(ConfigQueryDTO dto);

    List<ConfigVO> listConfigs(String group);

    ConfigVO getConfig(String group, String key);

    Integer getConfigVersion(String group, String key);

}
