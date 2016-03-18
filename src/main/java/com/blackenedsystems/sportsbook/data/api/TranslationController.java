package com.blackenedsystems.sportsbook.data.api;

import com.blackenedsystems.sportsbook.data.SportsbookDataImporterConfiguration;
import com.blackenedsystems.sportsbook.data.internal.TranslationService;
import com.blackenedsystems.sportsbook.data.internal.model.EntityType;
import com.blackenedsystems.sportsbook.data.internal.model.Translation;
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
 * @since 18/03/16
 */
@Controller
@RequestMapping(
        value = "/api/translation",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @RequestMapping(
            value = "/list/{type}",
            method = RequestMethod.GET
    )
    public ResponseEntity loadCategoryList(@PathVariable("type") EntityType entityType,
                                           @RequestParam(value = "lc", required = false, defaultValue = SportsbookDataImporterConfiguration.DEFAULT_LANGUAGE) String languageCode) {
        List<Translation> translationList = translationService.loadTranslations(entityType, languageCode);
        return ResponseEntity.ok(translationList);
    }

    @RequestMapping(
            value = "/list/{type}/{key}",
            method = RequestMethod.GET
    )
    public ResponseEntity loadCategoryList(@PathVariable("type") EntityType entityType,
                                           @PathVariable("key") int entityKey) {
        List<Translation> translationList = translationService.loadTranslations(entityType, entityKey);
        return ResponseEntity.ok(translationList);
    }
}
