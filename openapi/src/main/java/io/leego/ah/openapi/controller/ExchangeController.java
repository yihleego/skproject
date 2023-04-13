package io.leego.ah.openapi.controller;

import io.leego.ah.openapi.annotation.Privileged;
import io.leego.ah.openapi.dto.ExchangeSaveDTO;
import io.leego.ah.openapi.service.ExchangeService;
import io.leego.ah.openapi.vo.ExchangeVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("exchanges")
public class ExchangeController {
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    /**
     * Saves all exchanges.
     *
     * @param dto the exchanges to be saved.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveExchanges(@RequestBody ExchangeSaveDTO dto) {
        exchangeService.saveExchanges(dto);
    }

    /**
     * Returns the latest exchange.
     *
     * @return the latest exchange.
     * @throws HttpClientErrorException.NotFound if the config is not found.
     */
    @Privileged
    @GetMapping("latest")
    @ResponseStatus(HttpStatus.OK)
    public ExchangeVO getLatestExchange() {
        return exchangeService.getLatestExchange();
    }

}
