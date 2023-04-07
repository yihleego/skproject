package io.leego.ah.openapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeSaveDTO {
    @NotNull
    private Integer lastPrice;
    @NotNull
    private OfferDTO[] buyOffers;
    @NotNull
    private OfferDTO[] sellOffers;
}
