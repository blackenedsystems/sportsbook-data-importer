package com.blackenedsystems.sportsbook.data;

import com.blackenedsystems.sportsbook.data.betfair.BetfairConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * Test that the spring application builds as expected and that we end up with the configuration we require.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestSportsbookDataImporterApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class SportsbookDataImporterApplicationTests {

    @Autowired
    ApplicationContext context;

	@Test
    public void verifyBetfairConfiguration() {
        BetfairConfiguration betfairConfiguration = (BetfairConfiguration) context.getBean("betfairConfiguration");
        assertEquals("test-betfair-cert-2048.p12", betfairConfiguration.loginCertFileName);
        assertEquals("password", betfairConfiguration.loginCertPassword);
        assertEquals("key", betfairConfiguration.apiKey);
    }

    @Test
    public void verifyDataSource() {
        DataSource dataSource = (DataSource) context.getBean("dataSource");
        String ds = dataSource.toString();
        assertTrue(ds.contains("jdbc:h2:./sports"));
        assertFalse(ds.contains("postgresql"));
    }

}
