package com.blackenedsystems.sportsbook.data.test;

/**
 * @author Alan Tibbetts
 * @since 7/3/16 16:00
 */
public abstract class AbstractDBTestExecutor implements DBTestExecutor {

    @Override
    public boolean hasInitialisationSql() {
        return getInitialisationSQL() != null && getInitialisationSQL().length > 0;
    }

    @Override
    public String[] getInitialisationSQL() {
        return new String[]{};
    }
}
