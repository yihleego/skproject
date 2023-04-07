package io.leego.ah.openapi.service;

import io.leego.ah.openapi.dto.ExchangeSaveDTO;
import io.leego.ah.openapi.vo.ExchangeVO;

/**
 * @author Leego Yih
 */
public interface ExchangeService {

    void saveExchanges(ExchangeSaveDTO dto);

    ExchangeVO getLatestExchange();

}
