package com.blackenedsystems.sportsbook.data.internal;

import com.blackenedsystems.sportsbook.data.internal.dao.CategoryDao;
import com.blackenedsystems.sportsbook.data.internal.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 16/03/16
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Category> loadCategories(final String languageCode) {
        return categoryDao.loadCategories(languageCode);
    }
}
