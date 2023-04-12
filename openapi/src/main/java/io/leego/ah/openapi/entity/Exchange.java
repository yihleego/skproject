package io.leego.ah.openapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "exchange")
public class Exchange implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(updatable = false)
    private Integer lastPrice;
    @Column(updatable = false)
    private String buyOffers;
    @Column(updatable = false)
    private String sellOffers;
    @Column(updatable = false)
    private Instant createdTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    public static class Offer {
        private Integer price;
        private Integer volume;
    }
}
