package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.SportsbookDataImporterApplication;
import com.blackenedsystems.sportsbook.data.internal.model.Market;
import com.blackenedsystems.sportsbook.data.internal.model.MarketType;
import com.blackenedsystems.sportsbook.data.test.AbstractDBTestExecutor;
import com.blackenedsystems.sportsbook.data.test.DBTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Alan Tibbetts
 * @since 18/03/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SportsbookDataImporterApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class MarketServiceDBTest extends DBTest {

    @Autowired
    private MarketService marketService;

    @Test
    public void loadMarkets_empty_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public void execute() {
                        List<Market> marketList = marketService.loadMarkets("en");
                        assertNotNull(marketList);
                        assertEquals(0, marketList.size());
                    }
                });
    }

    @Test
    public void loadMarkets_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO market (default_name, market_type, created, created_by, updated, updated_by) " +
                                        "VALUES ('Match Odds', 'WIN_DRAW_WIN', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')",
                                "INSERT INTO translation (entity_type, language_code, entity_key, translation, created, created_by, updated, updated_by) " +
                                        "VALUES ('MARKET_INSTRUCTION', 'en', '1', 'Help text', CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system');"
                        };
                    }

                    @Override
                    public void execute() {
                        List<Market> marketList = marketService.loadMarkets("en");
                        assertNotNull(marketList);
                        assertEquals(1, marketList.size());

                        Market market = marketList.get(0);
                        assertTrue(MarketType.WIN_DRAW_WIN == market.getMarketType());
                        assertEquals("Match Odds", market.getName());
                        assertEquals("Help text", market.getInstructions());
                    }
                });
    }

    @Test
    public void loadMarkets_withTranslations_ok() throws Exception {
        executeTest(
                new AbstractDBTestExecutor() {
                    @Override
                    public String[] getInitialisationSQL() {
                        return new String[]{
                                "INSERT INTO market (default_name, market_type, created, created_by, updated, updated_by) " +
                                        "VALUES ('Match Odds', 'WIN_DRAW_WIN', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system')",
                                "INSERT INTO translation (entity_type, language_code, entity_key, translation, created, created_by, updated, updated_by) " +
                                        "VALUES ('MARKET', 'de', '1', 'Spielergebnis', CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system');",
                                "INSERT INTO translation (entity_type, language_code, entity_key, translation, created, created_by, updated, updated_by) " +
                                        "VALUES ('MARKET_INSTRUCTION', 'de', '1', 'Hilfe', CURRENT_TIMESTAMP(), 'system', CURRENT_TIMESTAMP(), 'system');"
                        };
                    }

                    @Override
                    public void execute() {
                        List<Market> marketList = marketService.loadMarkets("de");
                        assertNotNull(marketList);
                        assertEquals(1, marketList.size());

                        Market market = marketList.get(0);
                        assertTrue(MarketType.WIN_DRAW_WIN == market.getMarketType());
                        assertEquals("Spielergebnis", market.getName());
                        assertEquals("Hilfe", market.getInstructions());
                    }
                });
    }
}
