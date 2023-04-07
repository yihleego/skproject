package io.leego.ah.openapi.controller;

import io.leego.ah.openapi.annotation.Privileged;
import io.leego.ah.openapi.dto.ItemQueryDTO;
import io.leego.ah.openapi.service.ItemService;
import io.leego.ah.openapi.util.Page;
import io.leego.ah.openapi.vo.ItemVO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Returns the items with the given query criteria.
     *
     * @param dto the query criteria.
     * @return the items.
     */
    @Privileged
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ItemVO> listItems(@Validated ItemQueryDTO dto) {
        return itemService.listItems(dto);
    }

}
