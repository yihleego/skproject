package io.leego.ah.openapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Entity
@Table(name = "auction")
public class Auction implements Persistable<Long>, Serializable {
    @Id
    private Long id;
    @Column(updatable = false)
    private String itemId;
    /** The buy price, if it is zero, it means there is no buy price. */
    private Integer buyPrice;
    /** The bid price. */
    private Integer bidPrice;
    /** @see io.leego.ah.openapi.constant.BidStatus */
    private String bidStatus;
    /** @see io.leego.ah.openapi.constant.TimeLeft */
    private String timeLeft;
    /** The count quantity. */
    private Integer count;
    /** Whether it is featured. */
    private Boolean featured;
    /** The remaining quantity is only available when it is featured. */
    private Integer remaining;
    /** The total quantity is only available when it is featured. */
    private Integer total;
    /** The variants, aka the UVs. */
    @Column(updatable = false)
    private String variant;
    /** The accessories. */
    @Column(updatable = false)
    private String accessory;
    /** Whether it is accurate. */
    private Boolean accurate;
    @Column(updatable = false)
    private Instant createdTime;
    private Instant updatedTime;
    /** The estimated end time, not guaranteed to be accurate. */
    private Instant estimatedEndTime;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemId", referencedColumnName = "id", insertable = false, updatable = false)
    private Item item;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Transient
    private transient boolean _new = true;

    @Override
    public boolean isNew() {
        return _new;
    }

    @PostLoad
    void postLoad() {
        this._new = false;
    }

    public void makeNew() {
        this._new = true;
    }

    public void makeNotNew() {
        this._new = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    public static class Variant {
        /** @see io.leego.ah.openapi.constant.VariantName */
        private String name;
        /** @see io.leego.ah.openapi.constant.VariantValue */
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    public static class Accessory {
        /** The accessory id is the item id */
        private String id;
        /** @see io.leego.ah.openapi.constant.ItemLocation */
        private String location;
    }
}
