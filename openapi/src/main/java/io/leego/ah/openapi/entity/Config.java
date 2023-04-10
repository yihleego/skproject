package io.leego.ah.openapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Leego Yih
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
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
