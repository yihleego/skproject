package io.leego.ah.openapi.controller;

import io.leego.ah.openapi.annotation.Privileged;
import io.leego.ah.openapi.constant.BidStatus;
import io.leego.ah.openapi.constant.CodeEnum;
import io.leego.ah.openapi.constant.ItemGroup;
import io.leego.ah.openapi.constant.ItemLocation;
import io.leego.ah.openapi.constant.TimeLeft;
import io.leego.ah.openapi.constant.VariantName;
import io.leego.ah.openapi.constant.VariantValue;
import io.leego.ah.openapi.util.Option;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("enums")
public class EnumController {
    public static final Map<String, List<Option<String, String>>> ENUMS;

    static {
        ENUMS = Map.of(
                BidStatus.class.getSimpleName(), CodeEnum.toOptions(BidStatus.values()),
                TimeLeft.class.getSimpleName(), CodeEnum.toOptions(TimeLeft.values()),
                ItemGroup.class.getSimpleName(), CodeEnum.toOptions(ItemGroup.values()),
                ItemLocation.class.getSimpleName(), CodeEnum.toOptions(ItemLocation.values()),
                VariantName.class.getSimpleName(), CodeEnum.toOptions(VariantName.values()),
                VariantValue.class.getSimpleName(), CodeEnum.toOptions(VariantValue.values())
        );
    }

    /**
     * Returns all enums.
     *
     * @return all enums.
     */
    @Privileged
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, List<Option<String, String>>> listAllEnums() {
        return ENUMS;
    }

}
