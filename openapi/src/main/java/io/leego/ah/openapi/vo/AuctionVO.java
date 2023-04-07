package io.leego.ah.openapi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
