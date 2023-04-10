package io.leego.ah.openapi.dto;

import io.leego.ah.openapi.constant.ConfigStatus;
import io.leego.ah.openapi.entity.QConfig;
import io.leego.ah.openapi.util.QPredicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ConfigQueryDTO implements Serializable {
    private List<String> group;
    private List<String> key;

    public QPredicate toPredicate() {
        return QPredicate.create()
                .and(QConfig.config.group::in, group)
                .and(QConfig.config.key::in, key)
                .and(QConfig.config.status::eq, ConfigStatus.ENABLED.getCode());
    }
}

