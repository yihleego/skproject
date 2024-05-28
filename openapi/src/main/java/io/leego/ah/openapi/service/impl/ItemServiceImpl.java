package io.leego.ah.openapi.service.impl;

import io.leego.ah.openapi.pojo.dto.ItemQueryDTO;
import io.leego.ah.openapi.pojo.vo.ItemVO;
import io.leego.ah.openapi.repository.ItemRepository;
import io.leego.ah.openapi.service.ItemService;
import io.leego.ah.openapi.util.Page;
import org.springframework.stereotype.Service;

/**
 * @author Leego Yih
 */
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Page<ItemVO> listItems(ItemQueryDTO dto) {
        return itemRepository.findAll(dto.toPredicate(), dto).map(ItemVO::from);
    }
}
