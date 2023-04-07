package io.leego.ah.openapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import io.leego.ah.openapi.entity.QAuction;
import io.leego.ah.openapi.entity.Variant;
import io.leego.ah.openapi.util.QPredicate;
import io.leego.ah.openapi.util.TrimUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuctionQueryDTO extends ItemQueryDTO {
    /** Minimum buy price. */
    private Integer minBuyPrice;
    /** Maximum buy price. */
    private Integer maxBuyPrice;
    /** Minimum bid price. */
    private Integer minBidPrice;
    /** Maximum bid price. */
    private Integer maxBidPrice;
    /** @see io.leego.ah.openapi.constant.BidStatus */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> bidStatus;
    /** @see io.leego.ah.openapi.constant.TimeLeft */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> timeLeft;
    /** @see io.leego.ah.openapi.constant.VariantName */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> variantName;
    /** @see io.leego.ah.openapi.constant.VariantValue */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> variantValue;
    /** Whether it is featured. */
    private Boolean featured;
    /** Whether it is uved. */
    private Boolean variant;
    /** Whether it is accessorized. */
    private Boolean accessorized;

    @Override
    public QPredicate toPredicate() {
        QPredicate predicate = super.toPredicate()
                .and(QAuction.auction.buyPrice::between, minBuyPrice, maxBuyPrice)
                .and(QAuction.auction.bidPrice::between, minBidPrice, maxBidPrice)
                .and(QAuction.auction.bidStatus::in, TrimUtils.trim(bidStatus))
                .and(QAuction.auction.timeLeft::in, TrimUtils.trim(timeLeft))
                .and(QAuction.auction.featured::eq, featured)
                .and(jsonSearch(variantName, Variant.Fields.name))
                .and(jsonSearch(variantValue, Variant.Fields.value));
        if (variant != null) {
            predicate.and(variant ? QAuction.auction.variant.isNotNull() : QAuction.auction.variant.isNull());
        }
        if (accessorized != null) {
            predicate.and(accessorized ? QAuction.auction.accessory.isNotNull() : QAuction.auction.accessory.isNull());
        }
        return predicate;
    }

    private BooleanExpression jsonSearch(List<String> list, String k) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return Expressions.anyOf(list.stream()
                .filter(StringUtils::hasText)
                .map(v -> Expressions.booleanTemplate("JSON_SEARCH(variant, 'all', {0}, NULL, {1}) IS NOT NULL", v, "$**." + k))
                .toList()
                .toArray(new BooleanTemplate[0]));
    }
}

