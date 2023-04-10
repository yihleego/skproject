package io.leego.ah.openapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

import java.util.List;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemQueryDTO extends PageRequest {
    private List<String> id;
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
                .and(QItem.item.id::in, id)
                .and(QItem.item.name::likeIgnoreCase, TrimUtils.trim(name, s -> s.length() >= 2 ? "%" + s + "%" : null))
                .and(QItem.item.group::in, TrimUtils.trim(group))
                .and(QItem.item.location::in, TrimUtils.trim(location))
                .and(QItem.item.star::in, TrimUtils.trim(star));
    }
}

