package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.internal.dao.CompetitionDao;
import com.blackenedsystems.sportsbook.data.internal.model.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 2016-03-17,  2:49 PM
 */
@Service
public class CompetitionService {

    @Autowired
    private CompetitionDao competitionDao;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Competition> loadCompetitions(final int categoryId, final String languageCode) {
        return competitionDao.loadCompetitions(categoryId, languageCode);
    }
}
