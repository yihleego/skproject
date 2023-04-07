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
public class AuctionSaveDTO {
    @NotNull
    private AuctionDTO[] all;
    @NotNull
    private AuctionDTO[] ended;
}
