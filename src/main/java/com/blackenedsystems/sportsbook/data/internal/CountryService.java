package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.internal.dao.CountryDao;
import com.blackenedsystems.sportsbook.data.internal.model.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 16/03/16
 */
@Service
public class CountryService {

    @Autowired
    private CountryDao countryDao;

    public List<Country> loadCountries(final String languageCode) {
        return countryDao.loadCountries(languageCode);
    }
}
