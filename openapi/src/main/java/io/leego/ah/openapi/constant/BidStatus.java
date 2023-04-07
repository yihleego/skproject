package io.leego.ah.openapi.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum BidStatus implements CodeEnum<String, String> {
    NO_BID("NO_BID", "No Bid", true),
    BID("BID", "Bid", true),
    HIGH_BIDDER("HIGH_BIDDER", "High Bidder", false),
    OUTBID("OUTBID", "Outbid", false),
    WATCHING("WATCHING", "Watching", false),
    ;
    private final String code;
    private final String name;
    private final boolean visible;
}
