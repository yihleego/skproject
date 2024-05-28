package io.leego.ah.openapi.pojo.vo;

import io.leego.ah.openapi.entity.Item;
import io.leego.ah.openapi.util.JSONUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVO {
    private String id;
    private String name;
    private String group;
    private String icon;
    private String description;
    private Integer star;
    private Boolean level;
    private String location;
    private ColorizationVO[] colorization;

    public static ItemVO from(Item item) {
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(item, itemVO);
        itemVO.setColorization(JSONUtils.parseArray(item.getColorization(), ColorizationVO.class));
        return itemVO;
    }
}
