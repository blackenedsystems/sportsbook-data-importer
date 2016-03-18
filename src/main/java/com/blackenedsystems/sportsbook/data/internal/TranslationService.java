package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.internal.dao.TranslationDao;
import com.blackenedsystems.sportsbook.data.internal.model.EntityType;
import com.blackenedsystems.sportsbook.data.internal.model.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 18/03/16
 */
@Service
public class TranslationService {

    @Autowired
    private TranslationDao translationDao;

    /**
     * @param entityType entity type for which to load translations, e.g. CATEGORY, COMPETITION, etc
     * @param languageCode required language
     * @return a list of translations for the given entity and language.
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Translation> loadTranslations(final EntityType entityType, final String languageCode) {
        return translationDao.loadTranslations(entityType, languageCode);
    }

    /**
     * @param entityType entity type for which to load translations, e.g. CATEGORY, COMPETITION, etc
     * @param entityKey the key of the particular entity, e.g. the id of Football
     * @return a list of translations in various languages for the supplied entity (type/key)
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Translation> loadTranslations(final EntityType entityType, final int entityKey) {
        return translationDao.loadTranslations(entityType, entityKey);
    }
}
