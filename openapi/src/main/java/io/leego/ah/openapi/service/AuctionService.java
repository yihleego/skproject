package io.leego.ah.openapi.service;

import io.leego.ah.openapi.pojo.dto.AuctionQueryDTO;
import io.leego.ah.openapi.pojo.dto.AuctionSaveDTO;
import io.leego.ah.openapi.pojo.vo.AuctionVO;
import io.leego.ah.openapi.util.Page;

/**
 * @author Leego Yih
 */
public interface AuctionService {

    void saveAuctions(AuctionSaveDTO dto);

    Page<AuctionVO> listAuctions(AuctionQueryDTO dto);

}
