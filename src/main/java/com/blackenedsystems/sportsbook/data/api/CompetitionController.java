package com.blackenedsystems.sportsbook.data.api;

import com.blackenedsystems.sportsbook.data.internal.CompetitionService;
import com.blackenedsystems.sportsbook.data.internal.model.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 2016-03-17,  2:45 PM
 */
@Controller
@RequestMapping(
        value = "/api/competition",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @RequestMapping(
            value = "/list/{categoryId}",
            method = RequestMethod.GET
    )
    public ResponseEntity loadCompetitions(@PathVariable("categoryId") int categoryId,
                                           @RequestParam(value = "lang", required = false, defaultValue = "en") String languageCode) {
        List<Competition> competitionList = competitionService.loadCompetitions(categoryId, languageCode);
        return ResponseEntity.ok(competitionList);
    }
}
