package io.leego.ah.openapi.service;

import io.leego.ah.openapi.pojo.dto.ExchangeSaveDTO;
import io.leego.ah.openapi.pojo.vo.ExchangeVO;

/**
 * @author Leego Yih
 */
public interface ExchangeService {

    void saveExchanges(ExchangeSaveDTO dto);

    ExchangeVO getLatestExchange();

}
