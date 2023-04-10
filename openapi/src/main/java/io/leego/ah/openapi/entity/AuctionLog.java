package io.leego.ah.openapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

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
@Table(name = "auction_log")
public class AuctionLog implements Serializable {
    @Id
    private Long id;
    @Column(updatable = false)
    private String auctionId;
    private Integer buyPrice;
    private Integer bidPrice;
    private Integer status;
    private Instant createdTime;
    private Instant updatedTime;
}
