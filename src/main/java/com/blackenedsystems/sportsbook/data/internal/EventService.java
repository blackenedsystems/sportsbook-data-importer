package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.internal.dao.EventDao;
import com.blackenedsystems.sportsbook.data.internal.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 30/03/16
 */
@Service
public class EventService {

    @Autowired
    private EventDao eventDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Event save(final Event internalEvent, final String createdBy) {
        return eventDao.save(internalEvent, createdBy);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Event> loadEvents(final int competitionId, final String languageCode) {
        return eventDao.loadEvents(competitionId, languageCode);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Event> loadUpcomingEvents(final int competitionId, final String languageCode) {
        return eventDao.loadUpcomingEvents(competitionId, languageCode);
    }
}
