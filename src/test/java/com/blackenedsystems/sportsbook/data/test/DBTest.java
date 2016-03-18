package com.blackenedsystems.sportsbook.data.test;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Alan Tibbetts
 * @since 7/3/16 15:22
 */
public class DBTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Builds a test database using Liquibase. If specified, inserts initial data set, then executes the actual
     * test(s).  When the tests are complete, the database is rolled back; every test should start with a clean database.
     *
     * @param dbTestExecutor object containing the test(s) and initial data.
     * @throws SQLException
     * @throws LiquibaseException
     */
    protected void executeTest(final AbstractDBTestExecutor dbTestExecutor) throws SQLException, LiquibaseException {
        try (Connection connection = dataSource.getConnection()) {
            Liquibase liquibase = loadDatabase(connection);

            if (dbTestExecutor.hasInitialisationSql()) {
                initialiseData(dbTestExecutor.getInitialisationSQL());
            }

            try {
                dbTestExecutor.execute();
            } finally {
                liquibase.dropAll();
            }
        }
    }

    private void initialiseData(final String[] initialisationSQL) {
        for (String sql : initialisationSQL) {
            jdbcTemplate.execute(sql);
        }
    }

    private Liquibase loadDatabase(final Connection connection) throws LiquibaseException {
        Database database = DatabaseFactory
                .getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase("master.xml",
                new FileSystemResourceAccessor("src/main/resources/liquibase"),
                database);
        liquibase.update("*");
        return liquibase;
    }

}
