package io.leego.ah.openapi.controller;

import io.leego.ah.openapi.annotation.Privileged;
import io.leego.ah.openapi.dto.AuctionQueryDTO;
import io.leego.ah.openapi.dto.AuctionSaveDTO;
import io.leego.ah.openapi.service.AuctionService;
import io.leego.ah.openapi.util.Page;
import io.leego.ah.openapi.vo.AuctionVO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("auctions")
public class AuctionController {
    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    /**
     * Saves all auctions.
     *
     * @param dto the auctions to be saved.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void saveAuctions(@RequestBody AuctionSaveDTO dto) {
        auctionService.saveAuctions(dto);
    }

    /**
     * Returns the auctions with the given query criteria.
     *
     * @param dto the query criteria.
     * @return the auctions.
     */
    @Privileged
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<AuctionVO> listAuctions(@Validated AuctionQueryDTO dto) {
        return auctionService.listAuctions(dto);
    }

}
