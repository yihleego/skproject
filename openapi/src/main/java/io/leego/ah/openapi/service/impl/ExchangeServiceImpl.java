package io.leego.ah.openapi.service.impl;

import io.leego.ah.openapi.entity.Exchange;
import io.leego.ah.openapi.pojo.dto.ExchangeSaveDTO;
import io.leego.ah.openapi.pojo.vo.ExchangeVO;
import io.leego.ah.openapi.repository.ExchangeRepository;
import io.leego.ah.openapi.service.DataSyncService;
import io.leego.ah.openapi.service.ExchangeService;
import io.leego.ah.openapi.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
@Service
public class ExchangeServiceImpl implements ExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeServiceImpl.class);
    private final ExchangeRepository exchangeRepository;
    private final DataSyncService dataSyncService;

    public ExchangeServiceImpl(ExchangeRepository exchangeRepository, DataSyncService dataSyncService) {
        this.exchangeRepository = exchangeRepository;
        this.dataSyncService = dataSyncService;
    }

    @Override
    public void saveExchanges(ExchangeSaveDTO dto) {
        long begin = System.currentTimeMillis();
        Exchange exchange = new Exchange(null,
                dto.getLastPrice(),
                JSONUtils.toString(dto.getBuyOffers()),
                JSONUtils.toString(dto.getSellOffers()),
                Instant.now());
        exchangeRepository.save(exchange);
        long end = System.currentTimeMillis();
        logger.info("Created exchange in {} ms", end - begin);
        dataSyncService.create(List.of(exchange), "create exchange");
    }

    @Override
    public ExchangeVO getLatestExchange() {
        return exchangeRepository.findFirstByOrderByIdDesc()
                .map(ExchangeVO::from)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }
}
