package io.leego.ah.openapi.entity;

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
@Table(name = "config")
public class Config implements Serializable {
    @Id
    private Long id;
    private String group;
    private String key;
    private String value;
    private Integer version;
    private Integer status;
    private Instant createdTime;
    private Instant updatedTime;
}
