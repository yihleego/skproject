package io.leego.ah.openapi.service;

import io.leego.ah.openapi.dto.ItemQueryDTO;
import io.leego.ah.openapi.util.Page;
import io.leego.ah.openapi.vo.ItemVO;

/**
 * @author Leego Yih
 */
public interface ItemService {

    Page<ItemVO> listItems(ItemQueryDTO dto);

}
