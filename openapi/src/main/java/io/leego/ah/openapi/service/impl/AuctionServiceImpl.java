package io.leego.ah.openapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.ah.openapi.constant.BidStatus;
import io.leego.ah.openapi.constant.TimeLeft;
import io.leego.ah.openapi.dto.AuctionDTO;
import io.leego.ah.openapi.dto.AuctionQueryDTO;
import io.leego.ah.openapi.dto.AuctionSaveDTO;
import io.leego.ah.openapi.entity.Auction;
import io.leego.ah.openapi.entity.AuctionLog;
import io.leego.ah.openapi.entity.Item;
import io.leego.ah.openapi.repository.AuctionLogRepository;
import io.leego.ah.openapi.repository.AuctionRepository;
import io.leego.ah.openapi.repository.ItemRepository;
import io.leego.ah.openapi.service.AuctionService;
import io.leego.ah.openapi.service.DataSyncService;
import io.leego.ah.openapi.util.Page;
import io.leego.ah.openapi.vo.AccessoryVO;
import io.leego.ah.openapi.vo.AuctionVO;
import io.leego.ah.openapi.vo.ColorizationVO;
import io.leego.ah.openapi.vo.ItemVO;
import io.leego.ah.openapi.vo.VariantVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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

/**
 * @author Leego Yih
 */
@Service
public class AuctionServiceImpl extends BaseServiceImpl implements AuctionService {
    private static final Logger logger = LoggerFactory.getLogger(AuctionServiceImpl.class);
    private final AuctionRepository auctionRepository;
    private final AuctionLogRepository auctionLogRepository;
    private final ItemRepository itemRepository;
    private final DataSyncService dataSyncService;
    private final ExecutorService executorService;

    public AuctionServiceImpl(ObjectMapper objectMapper, AuctionRepository auctionRepository, AuctionLogRepository auctionLogRepository, ItemRepository itemRepository, DataSyncService dataSyncService) {
        super(objectMapper);
        this.auctionRepository = auctionRepository;
        this.auctionLogRepository = auctionLogRepository;
        this.itemRepository = itemRepository;
        this.dataSyncService = dataSyncService;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void saveAuctions(AuctionSaveDTO dto) {
        // Auctions `all` come from search results.
        final AuctionDTO[] all = dto.getAll();
        // Auctions `ended` come from `my bids`, which means that the final bid prices are accurate.
        final AuctionDTO[] ended = dto.getEnded();
        executorService.execute(() -> {
            logger.info("Saving auctions(all={}, ended={})", all.length, ended.length);
            saveAuctionsAsync(all, ended);
            logger.info("Saved auctions(all={}, ended={})", all.length, ended.length);
        });
    }

    private void saveAuctionsAsync(AuctionDTO[] all, AuctionDTO[] ended) {
        // Collect all ids
        List<Long> ids = new ArrayList<>(all.length + ended.length);
        for (AuctionDTO o : all) {
            trimAuction(o);
            ids.add(o.getId());
        }
        for (AuctionDTO o : ended) {
            // Filter out the auctions that have ended
            if (!Objects.equals(o.getTimeLeft(), TimeLeft.ENDED.getCode())) {
                continue;
            }
            trimAuction(o);
            ids.add(o.getId());
        }

        // Find the auctions that exist in the database but do not exist in the given ids,
        // which means they are ended. (someone bought it or the time is up)
        // There is no guarantee that these final bid prices will be accurate.
        List<Auction> ends = auctionRepository.findByIdNotInAndTimeLeftIn(ids, NONE_ENDED);

        // Find all auctions with the given ids
        Map<Long, Auction> oldMap = auctionRepository.findAllById(ids)
                .stream().collect(Collectors.toMap(Auction::getId, Function.identity()));

        Instant now = Instant.now();
        List<Auction> creates = new ArrayList<>();
        List<Auction> updates = new ArrayList<>();
        List<Long> skips = new ArrayList<>(ids.size());
        Set<Long> dupes = new HashSet<>();
        List<AuctionLog> logs = new ArrayList<>();

        // Mark the auctions as ended (NOT from my bids)
        for (Auction o : ends) {
            o.setTimeLeft(TimeLeft.ENDED.getCode());
            logs.add(buildAuctionLog(o, now));
        }

        // Update the auctions as ended (from my bids)
        for (AuctionDTO dto : ended) {
            Auction old = oldMap.get(dto.getId());
            // Skip if the auction does not change
            if (!isAnyChanged(dto, old)) {
                continue;
            }
            // Remove if the same auction is present
            if (!dupes.add(dto.getId())) {
                logger.warn("Duplicate auction from ended: {}", dto);
                continue;
            }
            Auction cur = buildAuction(dto, old, now, true);
            if (old == null) {
                creates.add(cur);
            } else {
                updates.add(cur);
            }
            logs.add(buildAuctionLog(cur, now));
        }

        for (AuctionDTO dto : all) {
            Auction old = oldMap.get(dto.getId());
            // Update time only if the auction does not change
            if (!isAnyChanged(dto, old)) {
                skips.add(old.getId());
                continue;
            }
            // Remove if the same auction is present
            if (!dupes.add(dto.getId())) {
                logger.warn("Duplicate auction from all: {}", dto);
                continue;
            }
            Auction cur = buildAuction(dto, old, now, false);
            if (old == null) {
                creates.add(cur);
            } else {
                updates.add(cur);
            }
            logs.add(buildAuctionLog(cur, now));
        }

        // End the auctions
        if (!ends.isEmpty()) {
            long begin = System.currentTimeMillis();
            List<Long> endIds = ends.stream().map(Auction::getId).toList();
            int count = auctionRepository.updateLeftTime(endIds, TimeLeft.ENDED.getCode(), Instant.now());
            long end = System.currentTimeMillis();
            if (ends.size() == count) {
                logger.info("Ended auctions({}) in {} ms", count, end - begin);
            } else {
                logger.warn("Ended auctions({}->{}) in {} ms", ends.size(), count, end - begin);
            }
        }
        // Create new auctions
        if (!creates.isEmpty()) {
            long begin = System.currentTimeMillis();
            auctionRepository.saveAllAndFlush(creates);
            long end = System.currentTimeMillis();
            logger.info("Created auctions({}) in {} ms", creates.size(), end - begin);
        }
        // Update the auctions
        if (!updates.isEmpty()) {
            long begin = System.currentTimeMillis();
            auctionRepository.saveAllAndFlush(updates);
            long end = System.currentTimeMillis();
            logger.info("Updated auctions({}) in {} ms", updates.size(), end - begin);
        }
        // Update time only if the auctions haven't changed
        if (!skips.isEmpty()) {
            long begin = System.currentTimeMillis();
            int count = auctionRepository.updateUpdatedTime(skips, Instant.now());
            long end = System.currentTimeMillis();
            if (skips.size() == count) {
                logger.info("Skipped auctions({}) in {} ms", count, end - begin);
            } else {
                logger.warn("Skipped auctions({}->{}) in {} ms", skips.size(), count, end - begin);
            }
        }
        // Create logs
        if (!logs.isEmpty()) {
            long begin = System.currentTimeMillis();
            auctionLogRepository.saveAll(logs);
            long end = System.currentTimeMillis();
            logger.info("Created auction logs({}) in {} ms", logs.size(), end - begin);
        }
        // Sync data
        dataSyncService.update(ends, "end auction");
        dataSyncService.create(creates, "create auction");
        dataSyncService.update(updates, "update auction");
        dataSyncService.create(logs, "create auction log");
        // Clear
        dupes.clear();
    }

    private void trimAuction(AuctionDTO dto) {
        // Only two statues will be saved: `NO_BID` and `BID`
        if (!Objects.equals(dto.getBidStatus(), BidStatus.NO_BID.getCode())) {
            dto.setBidStatus(BidStatus.BID.getCode());
        }
    }

    private Auction buildAuction(AuctionDTO dto, Auction old, Instant now, Boolean accurate) {
        Auction cur = new Auction();
        BeanUtils.copyProperties(dto, cur);
        cur.setCreatedTime(now);
        cur.setUpdatedTime(now);
        cur.setAccurate(accurate);
        TimeLeft timeLeft = TimeLeft.valueOf(cur.getTimeLeft());
        if (old == null) {
            // Create if the original auction does not exist
            cur.makeNew();
            cur.setVariant(writeValue(dto.getVariant()));
            cur.setAccessory(writeValue(dto.getAccessory()));
            // Featured auctions have a duration that may last up to 4 days
            cur.setEstimatedEndTime(now.plus(timeLeft.getDuration(cur.getFeatured())));
        } else {
            // Update if the original auction exists
            cur.makeNotNew();
            cur.setVariant(old.getVariant());
            cur.setAccessory(old.getAccessory());
            cur.setEstimatedEndTime(old.getEstimatedEndTime());
            if (timeLeft == TimeLeft.VERY_SHORT && !Objects.equals(cur.getBidPrice(), old.getBidPrice())) {
                // Time is extended with every new bid when the `timeLeft` equals `VERY_SHORT`
                cur.setEstimatedEndTime(now.plus(timeLeft.getDuration()));
            } else if (!cur.getTimeLeft().equals(old.getTimeLeft())) {
                // Recalculate the `estimatedEndTime` if the `timeLeft` changed
                Instant newEstimatedEndTime = now.plus(timeLeft.getDuration(cur.getFeatured()));
                cur.setEstimatedEndTime(newEstimatedEndTime);
            }
        }
        return cur;
    }

    private AuctionLog buildAuctionLog(Auction o, Instant now) {
        return new AuctionLog(null, o.getId(), o.getBidPrice(), o.getBidStatus(), o.getTimeLeft(), now);
    }

    /** Returns true if there is any change with {@code bidPrice}, {@code bidStatus}, {@code timeLeft} or {@code remaining}. */
    private boolean isAnyChanged(AuctionDTO dto, Auction old) {
        // Only the following values are mutable
        return old == null
                || !Objects.equals(old.getBidPrice(), dto.getBidPrice())
                || !Objects.equals(old.getBidStatus(), dto.getBidStatus())
                || !Objects.equals(old.getTimeLeft(), dto.getTimeLeft())
                || !Objects.equals(old.getRemaining(), dto.getRemaining());
    }

    /** Returns true if there is any change with {@code bidStatus} or {@code timeLeft}. */
    private boolean isStatusChanged(AuctionDTO dto, Auction old) {
        // Check bidStatus and timeLeft
        return old == null
                || !Objects.equals(old.getBidStatus(), dto.getBidStatus())
                || !Objects.equals(old.getTimeLeft(), dto.getTimeLeft());
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
        if (page.isEmpty()) {
            return Page.empty(dto);
        }
        // Hold items and accessories for filling data later
        List<ItemVO> items = new ArrayList<>(page.getSize());
        List<AccessoryVO> accessories = new ArrayList<>();
        Page<AuctionVO> newPage = page.map(auction -> {
            AuctionVO auctionVO = toVO(auction);
            items.add(auctionVO.getItem());
            if (auctionVO.getAccessory() != null) {
                Collections.addAll(accessories, auctionVO.getAccessory());
            }
            return auctionVO;
        });
        // Find all items and accessories
        List<String> itemIds = new ArrayList<>(items.size() + accessories.size());
        itemIds.addAll(items.stream().map(ItemVO::getId).toList());
        itemIds.addAll(accessories.stream().map(AccessoryVO::getId).toList());
        Map<String, Item> itemMap = itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        for (ItemVO vo : items) {
            Item item = itemMap.get(vo.getId());
            if (item != null) {
                BeanUtils.copyProperties(item, vo);
                vo.setColorization(readValue(item.getColorization(), ColorizationVO[].class));
            }
        }
        for (AccessoryVO vo : accessories) {
            Item accessory = itemMap.get(vo.getId());
            if (accessory != null) {
                BeanUtils.copyProperties(accessory, vo);
                vo.setColorization(readValue(accessory.getColorization(), ColorizationVO[].class));
            }
        }
        return newPage;
    }

    private AuctionVO toVO(Auction auction) {
        AuctionVO auctionVO = new AuctionVO();
        BeanUtils.copyProperties(auction, auctionVO);
        // Set item
        ItemVO itemVO = new ItemVO();
        itemVO.setId(auction.getItemId());
        auctionVO.setItem(itemVO);
        // Set variants
        VariantVO[] variants = readValue(auction.getVariant(), VariantVO[].class);
        auctionVO.setVariant(variants);
        // Set accessories
        AccessoryVO[] accessories = readValue(auction.getAccessory(), AccessoryVO[].class);
        auctionVO.setAccessory(accessories);
        return auctionVO;
    }
}
