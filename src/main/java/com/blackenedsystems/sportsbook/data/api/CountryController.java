package com.blackenedsystems.sportsbook.data.api;

import com.blackenedsystems.sportsbook.data.internal.CountryService;
import com.blackenedsystems.sportsbook.data.internal.model.Country;
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
 * @since 16/03/16
 */
@Controller
@RequestMapping(
        value = "/api/country",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class CountryController {

    @Autowired
    private CountryService countryService;

    @RequestMapping(
            value = "/list",
            method = RequestMethod.GET
    )
    public ResponseEntity loadCountryList(@RequestParam(value = "lang", required = false, defaultValue = "en") String languageCode) {
        List<Country> countryList = countryService.loadCountries(languageCode);
        return ResponseEntity.ok(countryList);
    }
}
