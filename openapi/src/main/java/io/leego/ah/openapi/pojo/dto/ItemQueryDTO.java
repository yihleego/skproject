package io.leego.ah.openapi.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.leego.ah.openapi.entity.QItem;
import io.leego.ah.openapi.util.PageRequest;
import io.leego.ah.openapi.util.QPredicate;
import io.leego.ah.openapi.util.TrimUtils;
import io.leego.ah.openapi.validation.Paged;
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
@Paged(maxPage = 1000, maxSize = 100)
public class ItemQueryDTO extends PageRequest {
    private List<String> id;
    /** Item name must be at least 2 characters. */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;
    /** @see io.leego.ah.openapi.enumeration.ItemGroup */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> group;
    /** @see io.leego.ah.openapi.enumeration.ItemLocation */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> location;
    /** Star ranges from <code>-1</code> to <code>5</code>, <code>-1</code> means no star. */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Integer> star;

    public QPredicate toPredicate() {
        return QPredicate.create()
                .and(QItem.item.id::in, id)
                .and(QItem.item.name::likeIgnoreCase, TrimUtils.trim(name, s -> s.length() >= 2 ? "%" + s + "%" : null))
                .and(QItem.item.group::in, TrimUtils.trim(group))
                .and(QItem.item.location::in, TrimUtils.trim(location))
                .and(QItem.item.star::in, TrimUtils.trim(star));
    }
}

