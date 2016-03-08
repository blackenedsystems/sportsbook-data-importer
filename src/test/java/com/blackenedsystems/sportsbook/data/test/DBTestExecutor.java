package com.blackenedsystems.sportsbook.data.test;

/**
 * @author Alan Tibbetts
 * @since 07/03/16
 */
public interface DBTestExecutor {

    /**
     * Contains the code to be executed in the context of the test.  NB. A new database will
     * be created before the code is executed and dropped afterward.
     */
    void execute();

    /**
     * @return true if this executor has initialisation sql.
     */
    boolean hasInitialisationSql();

    /**
     * SQL to pre-populate the database before the test code is executed.
     */
    String[] getInitialisationSQL();
}
