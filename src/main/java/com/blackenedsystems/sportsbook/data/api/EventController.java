package com.blackenedsystems.sportsbook.data.api;

import com.blackenedsystems.sportsbook.data.SportsbookDataImporterConfiguration;
import com.blackenedsystems.sportsbook.data.internal.EventService;
import com.blackenedsystems.sportsbook.data.internal.model.Event;
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
 * @since 30/03/16
 */
@Controller
@RequestMapping(
        value = "/api/event",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class EventController {

    @Autowired
    private EventService eventService;


    @RequestMapping(
            value = "/list/{competitionId}",
            method = RequestMethod.GET
    )
    public ResponseEntity loadEvents(@PathVariable("competitionId") int competitionId,
                                     @RequestParam(value = "lc", required = false, defaultValue = SportsbookDataImporterConfiguration.DEFAULT_LANGUAGE) String languageCode) {
        List<Event> eventList = eventService.loadEvents(competitionId, languageCode);
        return ResponseEntity.ok(eventList);
    }
}
