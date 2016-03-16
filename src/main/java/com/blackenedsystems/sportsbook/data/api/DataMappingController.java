package com.blackenedsystems.sportsbook.data.api;

import com.blackenedsystems.sportsbook.data.mapping.DataMappingService;
import com.blackenedsystems.sportsbook.data.mapping.model.DataMapping;
import com.blackenedsystems.sportsbook.data.mapping.model.ExternalDataSource;
import com.blackenedsystems.sportsbook.data.mapping.model.MappingType;
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
 * @since 15/03/16
 */
@Controller
@RequestMapping(
        value = "/api/data_mapping",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class DataMappingController {

    @Autowired
    private DataMappingService dataMappingService;

    /**
     * @param externalDataSource original source of the data, e.g. BETFAIR
     * @param mappingType the type of mappings to load, e.g. CATEGORY, COMPETITION, etc
     * @param includeMapped include data that has already been mapped to internal structures?
     * @return a list of data mappings
     */
    @RequestMapping(
            value = "/list/{source}/{type}",
            method = RequestMethod.GET
    )
    public ResponseEntity loadDataMappingList(@PathVariable("source") ExternalDataSource externalDataSource,
                                              @PathVariable("type") MappingType mappingType,
                                              @RequestParam(value = "mapped", required = false, defaultValue = "false") boolean includeMapped) {

        List<DataMapping> dataMappings = dataMappingService.loadDataMappings(externalDataSource, mappingType, includeMapped);
        return ResponseEntity.ok(dataMappings);
    }
}
