package io.leego.ah.openapi;

import io.leego.ah.openapi.constant.ItemGroup;
import io.leego.ah.openapi.constant.VariantName;
import io.leego.ah.openapi.constant.VariantValue;
import io.leego.ah.openapi.dto.AuctionQueryDTO;
import io.leego.ah.openapi.dto.ConfigQueryDTO;
import io.leego.ah.openapi.dto.ItemQueryDTO;
import io.leego.ah.openapi.service.AuctionService;
import io.leego.ah.openapi.service.ConfigService;
import io.leego.ah.openapi.service.ExchangeService;
import io.leego.ah.openapi.service.ItemService;
import io.leego.ah.openapi.util.Page;
import io.leego.ah.openapi.vo.AuctionVO;
import io.leego.ah.openapi.vo.ConfigVO;
import io.leego.ah.openapi.vo.ExchangeVO;
import io.leego.ah.openapi.vo.ItemVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * @author Leego Yih
 */
@SpringBootTest
@ActiveProfiles("test")
public class OpenApiTests {
    private static final Logger logger = LoggerFactory.getLogger(OpenApiTests.class);
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private ConfigService configService;

    @BeforeEach
    public void before() {
    }

    @AfterEach
    public void after() {
    }

    @Test
    public void testListItems() {
        ItemQueryDTO dto = new ItemQueryDTO();
        dto.setPage(0);
        dto.setSize(10);
        dto.setName("calad");
        dto.setGroup(List.of(ItemGroup.SWORD.getName()));
        dto.setLocation(null);
        dto.setStar(List.of(5));
        Page<ItemVO> list = itemService.listItems(dto);
        for (ItemVO o : list) {
            logger.info("{}", o);
        }
    }

    @Test
    public void testListAuctions() {
        AuctionQueryDTO dto = new AuctionQueryDTO();
        dto.setPage(0);
        dto.setSize(10);
        dto.setName("Brandish");
        dto.setGroup(List.of(ItemGroup.SWORD.getName()));
        dto.setLocation(null);
        dto.setStar(List.of(2, 3, 4));
        dto.setVariantName(List.of(VariantName.ATTACK_SPEED_INCREASED.getCode()));
        dto.setVariantValue(List.of(VariantValue.MEDIUM.getCode()));
        Page<AuctionVO> list = auctionService.listAuctions(dto);
        for (AuctionVO o : list) {
            logger.info("{}", o);
        }
    }

    @Test
    public void testGetLatestExchange() {
        ExchangeVO o = exchangeService.getLatestExchange();
        logger.info("{}", o);
    }

    @Test
    public void testListConfigs() {
        ConfigQueryDTO dto = new ConfigQueryDTO();
        dto.setGroup(List.of("auctionbot"));
        List<ConfigVO> list = configService.listConfigs(dto);
        for (ConfigVO o : list) {
            logger.info("{}", o);
        }
    }

    @Test
    public void testListConfigsByGroup() {
        List<ConfigVO> list = configService.listConfigs("auctionbot");
        for (ConfigVO o : list) {
            logger.info("{}", o);
        }
    }

    @Test
    public void testGetConfig() {
        ConfigVO o = configService.getConfig("auctionbot", "autobid");
        logger.info("{}", o);
    }

}
