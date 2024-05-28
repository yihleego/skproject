package io.leego.ah.openapi.service;

import io.leego.ah.openapi.pojo.dto.ItemQueryDTO;
import io.leego.ah.openapi.pojo.vo.ItemVO;
import io.leego.ah.openapi.util.Page;

/**
 * @author Leego Yih
 */
public interface ItemService {

    Page<ItemVO> listItems(ItemQueryDTO dto);

}
