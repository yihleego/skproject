package io.leego.ah.openapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.ah.openapi.dto.ExchangeSaveDTO;
import io.leego.ah.openapi.entity.Exchange;
import io.leego.ah.openapi.event.EventType;
import io.leego.ah.openapi.event.SyncEvent;
import io.leego.ah.openapi.repository.ExchangeRepository;
import io.leego.ah.openapi.service.ExchangeService;
import io.leego.ah.openapi.vo.ExchangeVO;
import io.leego.ah.openapi.vo.OfferVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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
    public void saveExchanges(ExchangeSaveDTO dto) {
        Exchange exchange = new Exchange(null,
                dto.getLastPrice(),
                writeValue(dto.getBuyOffers()),
                writeValue(dto.getSellOffers()),
                Instant.now());
        exchangeRepository.save(exchange);
        eventPublisher.publishEvent(new SyncEvent(List.of(exchange), EventType.INSERT, "create exchange"));
    }

    @Override
    public ExchangeVO getLatestExchange() {
        Exchange exchange = exchangeRepository.findFirstByOrderByIdDesc();
        if (exchange == null) {
            return null;
        }
        return new ExchangeVO(
                exchange.getLastPrice(),
                readValue(exchange.getBuyOffers(), OfferVO[].class),
                readValue(exchange.getSellOffers(), OfferVO[].class),
                exchange.getCreatedTime());
    }
}
