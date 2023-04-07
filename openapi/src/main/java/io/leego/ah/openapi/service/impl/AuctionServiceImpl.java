package io.leego.ah.openapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.ah.openapi.constant.BidStatus;
import io.leego.ah.openapi.constant.TimeLeft;
import io.leego.ah.openapi.dto.AuctionDTO;
import io.leego.ah.openapi.dto.AuctionQueryDTO;
import io.leego.ah.openapi.dto.AuctionSaveDTO;
import io.leego.ah.openapi.entity.Auction;
import io.leego.ah.openapi.entity.Item;
import io.leego.ah.openapi.event.EventType;
import io.leego.ah.openapi.event.SyncEvent;
import io.leego.ah.openapi.repository.AuctionRepository;
import io.leego.ah.openapi.repository.ItemRepository;
import io.leego.ah.openapi.service.AuctionService;
import io.leego.ah.openapi.util.Page;
import io.leego.ah.openapi.vo.AccessoryVO;
import io.leego.ah.openapi.vo.AuctionVO;
import io.leego.ah.openapi.vo.ColorizationVO;
import io.leego.ah.openapi.vo.ItemVO;
import io.leego.ah.openapi.vo.VariantVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Leego Yih
 */
@Service
public class AuctionServiceImpl extends BaseServiceImpl implements AuctionService {
    private static final Logger logger = LoggerFactory.getLogger(AuctionServiceImpl.class);
    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ExecutorService executorService;

    public AuctionServiceImpl(ObjectMapper objectMapper, AuctionRepository auctionRepository, ItemRepository itemRepository, ApplicationEventPublisher eventPublisher) {
        super(objectMapper);
        this.auctionRepository = auctionRepository;
        this.itemRepository = itemRepository;
        this.eventPublisher = eventPublisher;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void saveAuctions(AuctionSaveDTO dto) {
        // Auctions `all` come from search results.
        final AuctionDTO[] all = dto.getAll();
        // Auctions `ended` come from `my bids`, which means that the final bid prices are accurate.
        final AuctionDTO[] ended = dto.getEnded();
        executorService.execute(() -> {
            logger.info("[ALL] Saving auctions(all={}, ended={})", all.length, ended.length);
            saveAuctionsAsync(all, ended);
            logger.info("[ALL] Saved auctions(all={}, ended={})", all.length, ended.length);
        });
    }

    private void saveAuctionsAsync(AuctionDTO[] all, AuctionDTO[] ended) {
        // Collect all ids
        List<Long> ids = Stream.of(Arrays.stream(all), Arrays.stream(ended).filter(o -> Objects.equals(o.getTimeLeft(), TimeLeft.ENDED.getCode())))
                .flatMap(o -> o)
                .peek(this::trimAuction)
                .map(AuctionDTO::getId)
                .toList();

        // Find the auctions that exist in the database but do not exist in the given ids,
        // which means they are ended. (someone bought it or the time is up)
        // There is no guarantee that these final bid prices will be accurate.
        List<Auction> endedList = auctionRepository.findByIdNotInAndTimeLeftIn(ids, NONE_ENDED);
        // Mark the auctions as ended
        endedList.forEach(o -> o.setTimeLeft(TimeLeft.ENDED.getCode()));

        // Find all auctions with the given ids
        Map<Long, Auction> oldMap = auctionRepository.findAllById(ids)
                .stream().collect(Collectors.toMap(Auction::getId, Function.identity()));

        Instant now = Instant.now();
        List<Auction> create = new ArrayList<>(128);
        List<Auction> update = new ArrayList<>(128);
        List<Long> skip = new ArrayList<>(ids.size());
        Set<Long> dupe = new HashSet<>(128);
        for (AuctionDTO dto : all) {
            Auction old = oldMap.get(dto.getId());
            // Update time only if the auction does not change
            if (!isAuctionChanged(dto, old)) {
                skip.add(old.getId());
                continue;
            }
            // Remove if the same auction is present
            if (!dupe.add(dto.getId())) {
                logger.warn("Duplicate auction from all: {}", dto);
                continue;
            }
            List<Auction> list = old == null ? create : update;
            list.add(buildAuction(dto, old, now));
        }

        for (AuctionDTO dto : ended) {
            Auction old = oldMap.get(dto.getId());
            // Skip if the auction does not change
            if (!isAuctionChanged(dto, old)) {
                continue;
            }
            // Remove if the same auction is present
            if (!dupe.add(dto.getId())) {
                logger.warn("Duplicate auction from ended: {}", dto);
                continue;
            }
            List<Auction> list = old == null ? create : update;
            list.add(buildAuction(dto, old, now));
        }

        // End the auctions
        endAuctions(endedList);
        // Create new auctions
        createAuctions(create);
        // Update the auctions
        updateAuctions(update);
        // Update time only if the auctions haven't changed
        skipAuctions(skip);
        // Clear
        dupe.clear();
    }

    private void trimAuction(AuctionDTO dto) {
        // Only two statues will be saved: `NO_BID` and `BID`
        if (!Objects.equals(dto.getBidStatus(), BidStatus.NO_BID.getCode())) {
            dto.setBidStatus(BidStatus.BID.getCode());
        }
    }

    private Auction buildAuction(AuctionDTO dto, Auction old, Instant now) {
        Auction cur = new Auction();
        BeanUtils.copyProperties(dto, cur);
        cur.setCreatedTime(now);
        cur.setUpdatedTime(now);
        TimeLeft timeLeft = TimeLeft.valueOf(cur.getTimeLeft());
        if (old == null) {
            // Create if the original auction does not exist
            cur.makeNew();
            cur.setVariant(writeValue(dto.getVariant()));
            cur.setAccessory(writeValue(dto.getAccessory()));
            // Featured auctions have a duration that may last up to 4 days
            cur.setEstimatedEndTime(now.plus(cur.getFeatured() ? timeLeft.getFeaturedDuration() : timeLeft.getMaxDuration()));
        } else {
            // Update if the original auction exists
            cur.makeNotNew();
            cur.setVariant(old.getVariant());
            cur.setAccessory(old.getAccessory());
            if (cur.getTimeLeft().equals(old.getTimeLeft())) {
                // Use the original `estimatedEndTime` if the `timeLeft` does not change
                cur.setEstimatedEndTime(old.getEstimatedEndTime());
            } else {
                // Recalculate the `estimatedEndTime`
                cur.setEstimatedEndTime(now.plus(cur.getFeatured() ? timeLeft.getFeaturedDuration() : timeLeft.getMaxDuration()));
            }
        }
        return cur;
    }

    private boolean isAuctionChanged(AuctionDTO dto, Auction old) {
        // Only the following values are mutable
        return old == null
                || !Objects.equals(old.getBidPrice(), dto.getBidPrice())
                || !Objects.equals(old.getBidStatus(), dto.getBidStatus())
                || !Objects.equals(old.getTimeLeft(), dto.getTimeLeft())
                || !Objects.equals(old.getRemaining(), dto.getRemaining());
    }

    private void createAuctions(List<Auction> list) {
        long begin = System.currentTimeMillis();
        if (!list.isEmpty()) {
            auctionRepository.saveAllAndFlush(list);
        }
        long end = System.currentTimeMillis();
        logger.info("Created auctions({}) in {} ms", list.size(), end - begin);
        eventPublisher.publishEvent(new SyncEvent(list, EventType.INSERT, "create auction"));
    }

    private void updateAuctions(List<Auction> list) {
        long begin = System.currentTimeMillis();
        if (!list.isEmpty()) {
            auctionRepository.saveAllAndFlush(list);
        }
        long end = System.currentTimeMillis();
        logger.info("Updated auctions({}) in {} ms", list.size(), end - begin);
        eventPublisher.publishEvent(new SyncEvent(list, EventType.UPDATE, "update auction"));
    }

    private void endAuctions(List<Auction> list) {
        long begin = System.currentTimeMillis();
        int count = 0;
        if (!list.isEmpty()) {
            List<Long> ids = list.stream().map(Auction::getId).toList();
            count = auctionRepository.updateLeftTime(ids, TimeLeft.ENDED.getCode(), Instant.now());
        }
        long end = System.currentTimeMillis();
        if (list.size() == count) {
            logger.info("Ended auctions({}) in {} ms", count, end - begin);
        } else {
            logger.warn("Ended auctions({}->{}) in {} ms", list.size(), count, end - begin);
        }
        eventPublisher.publishEvent(new SyncEvent(list, EventType.UPDATE, "end auction"));
    }

    private void skipAuctions(List<Long> list) {
        long begin = System.currentTimeMillis();
        int count = 0;
        if (!list.isEmpty()) {
            count = auctionRepository.updateUpdatedTime(list, Instant.now());
        }
        long end = System.currentTimeMillis();
        if (list.size() == count) {
            logger.info("Skipped auctions({}) in {} ms", count, end - begin);
        } else {
            logger.warn("Skipped auctions({}->{}) in {} ms", list.size(), count, end - begin);
        }
    }

    private static final List<String> NONE_ENDED = List.of(
            TimeLeft.VERY_SHORT.getCode(),
            TimeLeft.SHORT.getCode(),
            TimeLeft.MEDIUM.getCode(),
            TimeLeft.LONG.getCode(),
            TimeLeft.VERY_LONG.getCode());

    @Override
    public Page<AuctionVO> listAuctions(AuctionQueryDTO dto) {
        Page<Auction> page = auctionRepository.findAll(dto.toPredicate(), dto);

        List<ItemVO> allItems = new ArrayList<>(page.getSize());
        List<AccessoryVO> allAccessories = new ArrayList<>();
        Page<AuctionVO> newPage = page.map(auction -> {
            AuctionVO auctionVO = new AuctionVO();
            BeanUtils.copyProperties(auction, auctionVO);
            // Set item
            ItemVO itemVO = new ItemVO();
            itemVO.setId(auction.getItemId());
            auctionVO.setItem(itemVO);
            allItems.add(itemVO);
            // Set variants
            VariantVO[] variants = readValue(auction.getVariant(), VariantVO[].class);
            auctionVO.setVariant(variants);
            // Set accessories
            AccessoryVO[] accessories = readValue(auction.getAccessory(), AccessoryVO[].class);
            auctionVO.setAccessory(accessories);
            if (accessories != null) {
                Collections.addAll(allAccessories, accessories);
            }
            return auctionVO;
        });
        // Fill items
        if (!allItems.isEmpty()) {
            List<String> itemIds = allItems.stream().map(ItemVO::getId).distinct().toList();
            Map<String, Item> itemMap = itemRepository.findAllById(itemIds).stream()
                    .collect(Collectors.toMap(Item::getId, Function.identity()));
            for (ItemVO vo : allItems) {
                Item item = itemMap.get(vo.getId());
                if (item != null) {
                    BeanUtils.copyProperties(item, vo);
                    vo.setColorization(readValue(item.getColorization(), ColorizationVO[].class));
                }
            }
        }
        // Fill accessories
        if (!allAccessories.isEmpty()) {
            List<String> accessoryIds = allAccessories.stream().map(AccessoryVO::getId).distinct().toList();
            Map<String, Item> accessoryMap = itemRepository.findAllById(accessoryIds).stream()
                    .collect(Collectors.toMap(Item::getId, Function.identity()));
            for (AccessoryVO vo : allAccessories) {
                Item accessory = accessoryMap.get(vo.getId());
                if (accessory != null) {
                    BeanUtils.copyProperties(accessory, vo);
                    vo.setColorization(readValue(accessory.getColorization(), ColorizationVO[].class));
                }
            }
        }
        return newPage;
    }
}
