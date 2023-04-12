package io.leego.ah.openapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leego.ah.openapi.dto.ItemQueryDTO;
import io.leego.ah.openapi.entity.Item;
import io.leego.ah.openapi.repository.ItemRepository;
import io.leego.ah.openapi.service.ItemService;
import io.leego.ah.openapi.util.Page;
import io.leego.ah.openapi.vo.ColorizationVO;
import io.leego.ah.openapi.vo.ItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author Leego Yih
 */
@Service
public class ItemServiceImpl extends BaseServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ObjectMapper objectMapper, ItemRepository itemRepository) {
        super(objectMapper);
        this.itemRepository = itemRepository;
    }

    @Override
    public Page<ItemVO> listItems(ItemQueryDTO dto) {
        Page<Item> page = itemRepository.findAll(dto.toPredicate(), dto);
        return page.map(this::toVO);
    }

    private ItemVO toVO(Item item) {
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(item, itemVO);
        itemVO.setColorization(readValue(item.getColorization(), ColorizationVO[].class));
        return itemVO;
    }
}
