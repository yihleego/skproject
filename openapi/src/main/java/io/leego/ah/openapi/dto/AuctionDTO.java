package io.leego.ah.openapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionDTO {
    private Long id;
    private String itemId;
    private Integer buyPrice;
    private Integer bidPrice;
    private String bidStatus;
    private String timeLeft;
    private Integer count;
    private Boolean featured;
    private Integer remaining;
    private Integer total;
    private VariantDTO[] variant;
    private AccessoryDTO[] accessory;
}
