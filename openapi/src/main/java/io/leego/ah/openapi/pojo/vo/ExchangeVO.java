package io.leego.ah.openapi.pojo.vo;

import io.leego.ah.openapi.entity.Exchange;
import io.leego.ah.openapi.util.JSONUtils;
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

    public static ExchangeVO from(Exchange exchange) {
        return new ExchangeVO(
                exchange.getLastPrice(),
                JSONUtils.parseArray(exchange.getBuyOffers(), OfferVO.class),
                JSONUtils.parseArray(exchange.getSellOffers(), OfferVO.class),
                exchange.getCreatedTime());
    }
}
