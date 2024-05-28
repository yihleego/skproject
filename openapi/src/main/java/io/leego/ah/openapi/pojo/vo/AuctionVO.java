package io.leego.ah.openapi.pojo.vo;

import io.leego.ah.openapi.entity.Auction;
import io.leego.ah.openapi.util.JSONUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.Instant;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionVO {
    private Long id;
    private Integer buyPrice;
    private Integer bidPrice;
    private String bidStatus;
    private String timeLeft;
    private Integer count;
    private Boolean featured;
    private Integer remaining;
    private Integer total;
    private VariantVO[] variant;
    private AccessoryVO[] accessory;
    private Instant createdTime;
    private Instant updatedTime;
    private Instant estimatedEndTime;
    private ItemVO item;

    public static AuctionVO from(Auction auction) {
        AuctionVO auctionVO = new AuctionVO();
        BeanUtils.copyProperties(auction, auctionVO);
        // Set item
        ItemVO itemVO = new ItemVO();
        itemVO.setId(auction.getItemId());
        auctionVO.setItem(itemVO);
        // Set variants
        VariantVO[] variants = JSONUtils.parseArray(auction.getVariant(), VariantVO.class);
        auctionVO.setVariant(variants);
        // Set accessories
        AccessoryVO[] accessories = JSONUtils.parseArray(auction.getAccessory(), AccessoryVO.class);
        auctionVO.setAccessory(accessories);
        return auctionVO;
    }
}
