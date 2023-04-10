package io.leego.ah.openapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.ah.openapi.datasync.DataSyncEvent;
import io.leego.ah.openapi.dto.ExchangeSaveDTO;
import io.leego.ah.openapi.entity.Exchange;
import io.leego.ah.openapi.repository.ExchangeRepository;
import io.leego.ah.openapi.service.ExchangeService;
import io.leego.ah.openapi.vo.ExchangeVO;
import io.leego.ah.openapi.vo.OfferVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
@Service
public class ExchangeServiceImpl extends BaseServiceImpl implements ExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeServiceImpl.class);
    private final ExchangeRepository exchangeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ExchangeServiceImpl(ObjectMapper objectMapper, ExchangeRepository exchangeRepository, ApplicationEventPublisher eventPublisher) {
        super(objectMapper);
        this.exchangeRepository = exchangeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void saveExchanges(ExchangeSaveDTO dto) {
        long begin = System.currentTimeMillis();
        Exchange exchange = new Exchange(null,
                dto.getLastPrice(),
                writeValue(dto.getBuyOffers()),
                writeValue(dto.getSellOffers()),
                Instant.now());
        exchangeRepository.save(exchange);
        long end = System.currentTimeMillis();
        logger.info("Created exchange in {} ms", end - begin);
        eventPublisher.publishEvent(DataSyncEvent.insert(List.of(exchange), "create exchange"));
    }

    @Override
    public ExchangeVO getLatestExchange() {
        return exchangeRepository.findFirstByOrderByIdDesc()
                .map(this::toVO)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    private ExchangeVO toVO(Exchange exchange) {
        return new ExchangeVO(
                exchange.getLastPrice(),
                readValue(exchange.getBuyOffers(), OfferVO[].class),
                readValue(exchange.getSellOffers(), OfferVO[].class),
                exchange.getCreatedTime());
    }
}
