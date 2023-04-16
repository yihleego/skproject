package io.leego.ah.openapi.controller;

import io.leego.ah.openapi.datasync.DataSyncEvent;
import io.leego.ah.openapi.entity.Auction;
import io.leego.ah.openapi.entity.AuctionLog;
import io.leego.ah.openapi.entity.Exchange;
import io.leego.ah.openapi.entity.QAuction;
import io.leego.ah.openapi.entity.QAuctionLog;
import io.leego.ah.openapi.entity.QExchange;
import io.leego.ah.openapi.repository.AuctionLogRepository;
import io.leego.ah.openapi.repository.AuctionRepository;
import io.leego.ah.openapi.repository.ExchangeRepository;
import io.leego.ah.openapi.util.QPredicate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("sync")
public class DataSyncController {
    private final ApplicationEventPublisher publisher;
    private final AuctionRepository auctionRepository;
    private final AuctionLogRepository auctionLogRepository;
    private final ExchangeRepository exchangeRepository;

    public DataSyncController(ApplicationEventPublisher publisher, AuctionRepository auctionRepository, AuctionLogRepository auctionLogRepository, ExchangeRepository exchangeRepository) {
        this.publisher = publisher;
        this.auctionRepository = auctionRepository;
        this.auctionLogRepository = auctionLogRepository;
        this.exchangeRepository = exchangeRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sync(@RequestParam Instant time) {
        List<Auction> auctions = (List<Auction>) auctionRepository.findAll(
                QPredicate.create().and(QAuction.auction.updatedTime::after, time),
                Sort.by(Sort.Direction.DESC, Auction.Fields.id));
        List<AuctionLog> logs = (List<AuctionLog>) auctionLogRepository.findAll(
                QPredicate.create().and(QAuctionLog.auctionLog.createdTime::after, time),
                Sort.by(Sort.Direction.DESC, AuctionLog.Fields.id));
        List<Exchange> exchanges = (List<Exchange>) exchangeRepository.findAll(
                QPredicate.create().and(QExchange.exchange.createdTime::after, time),
                Sort.by(Sort.Direction.DESC, Exchange.Fields.id));
        syncData(auctions, 50, "sync auctions");
        syncData(logs, 100, "sync logs");
        syncData(exchanges, 100, "sync exchanges");
    }

    private void syncData(List<?> entities, int size, String tag) {
        if (entities.isEmpty()) {
            return;
        }
        partition(entities, size).forEach(list -> publisher.publishEvent(DataSyncEvent.update(list, tag)));
    }

    private static <E> List<List<E>> partition(List<E> list, final int size) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<E>> result;
        int fromIndex = 0;
        int toIndex = size;
        int total = list.size();
        if (size >= total) {
            result = new ArrayList<>();
            result.add(list);
        } else {
            int part = total / size;
            result = new ArrayList<>(part);
            for (int i = 0; i < part; i++) {
                result.add(list.subList(fromIndex, toIndex));
                fromIndex = toIndex;
                toIndex += size;
            }
            if (total > fromIndex) {
                result.add(list.subList(fromIndex, total));
            }
        }
        return result;
    }
}
