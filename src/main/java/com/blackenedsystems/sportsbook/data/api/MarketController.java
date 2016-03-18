package com.blackenedsystems.sportsbook.data.api;

import com.blackenedsystems.sportsbook.data.SportsbookDataImporterConfiguration;
import com.blackenedsystems.sportsbook.data.internal.MarketService;
import com.blackenedsystems.sportsbook.data.internal.model.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 18/03/16
 */
@Controller
@RequestMapping(
        value = "/api/market",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class MarketController {

    @Autowired
    private MarketService marketService;

    @RequestMapping(
            value = "/list",
            method = RequestMethod.GET
    )
    public ResponseEntity loadMarkets(@RequestParam(value = "lc", required = false, defaultValue = SportsbookDataImporterConfiguration.DEFAULT_LANGUAGE) String languageCode) {
        List<Market> marketList = marketService.loadMarkets(languageCode);
        return ResponseEntity.ok(marketList);
    }
}
