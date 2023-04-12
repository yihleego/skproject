package io.leego.ah.openapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import io.leego.ah.openapi.entity.Auction;
import io.leego.ah.openapi.entity.QAuction;
import io.leego.ah.openapi.entity.QItem;
import io.leego.ah.openapi.util.PageRequest;
import io.leego.ah.openapi.util.QPredicate;
import io.leego.ah.openapi.util.TrimUtils;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class AuctionQueryDTO extends PageRequest {
    private List<Long> id;
    /** Whether it is featured. */
    private Boolean featured;
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
    private List<String> accessory;

    /** Item name must be at least 2 characters. */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;
    /** @see io.leego.ah.openapi.constant.ItemGroup */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> group;
    /** @see io.leego.ah.openapi.constant.ItemLocation */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> location;
    /** Star ranges from <code>-1</code> to <code>5</code>, <code>-1</code> means no star. */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Integer> star;

    @Override @NotNull @Min(0) @Max(1000)
    public Integer getPage() {
        return page;
    }

    @Override @NotNull @Min(1) @Max(100)
    public Integer getSize() {
        return size;
    }

    public QPredicate toPredicate() {
        return QPredicate.create()
                .and(QAuction.auction.id::in, id)
                .and(QAuction.auction.featured::eq, featured)
                .and(QAuction.auction.buyPrice::between, minBuyPrice, maxBuyPrice)
                .and(QAuction.auction.bidPrice::between, minBidPrice, maxBidPrice)
                .and(QAuction.auction.bidStatus::in, TrimUtils.trim(bidStatus))
                .and(QAuction.auction.timeLeft::in, TrimUtils.trim(timeLeft))
                .and(jsonSearch(variantName, Auction.Fields.variant, Auction.Variant.Fields.name))
                .and(jsonSearch(variantValue, Auction.Fields.variant, Auction.Variant.Fields.value))
                .and(accessory == null ? null : accessory.contains("ANY")
                        ? QAuction.auction.accessory.isNotNull()
                        : jsonSearch(accessory, Auction.Fields.accessory, Auction.Accessory.Fields.id))
                .and(QItem.item.name::likeIgnoreCase, TrimUtils.trim(name, s -> s.length() >= 2 ? "%" + s + "%" : null))
                .and(QItem.item.group::in, TrimUtils.trim(group))
                .and(QItem.item.location::in, TrimUtils.trim(location))
                .and(QItem.item.star::in, TrimUtils.trim(star));
    }

    private BooleanExpression jsonSearch(List<String> list, final String column, final String k) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return Expressions.anyOf(list.stream()
                .filter(StringUtils::hasText)
                .map(v -> Expressions.booleanTemplate("JSON_SEARCH(" + column + ", 'all', {0}, NULL, '$**." + k + "') IS NOT NULL", v))
                .toList()
                .toArray(new BooleanTemplate[0]));
    }
}

