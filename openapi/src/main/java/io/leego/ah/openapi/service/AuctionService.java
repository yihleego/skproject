package io.leego.ah.openapi.service;

import io.leego.ah.openapi.dto.AuctionQueryDTO;
import io.leego.ah.openapi.dto.AuctionSaveDTO;
import io.leego.ah.openapi.util.Page;
import io.leego.ah.openapi.vo.AuctionVO;

/**
 * @author Leego Yih
 */
public interface AuctionService {

    void saveAuctions(AuctionSaveDTO dto);

    Page<AuctionVO> listAuctions(AuctionQueryDTO dto);

}
