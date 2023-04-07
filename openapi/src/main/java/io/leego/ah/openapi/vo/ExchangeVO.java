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
public class ExchangeVO {
    private Integer lastPrice;
    private OfferVO[] buyOffers;
    private OfferVO[] sellOffers;
    private Instant createdTime;
}
