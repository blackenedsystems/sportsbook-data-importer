package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.internal.dao.MarketDao;
import com.blackenedsystems.sportsbook.data.internal.model.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 18/03/16
 */
@Service
public class MarketService {

    @Autowired
    private MarketDao marketDao;

    public List<Market> loadMarkets(final String languageCode) {
        return marketDao.loadMarkets(languageCode);
    }
}
