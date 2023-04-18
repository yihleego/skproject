package io.leego.ah.openapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSyncDTO {
    private Instant beginTime;
    private Instant endTime;
    /** empty:all 0:auction 1:auction_log 2:exchange */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Integer> type;
}
