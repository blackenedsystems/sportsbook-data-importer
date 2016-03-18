package com.blackenedsystems.sportsbook.data.api;

import com.blackenedsystems.sportsbook.data.internal.CategoryService;
import com.blackenedsystems.sportsbook.data.internal.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 16/03/16
 */
@Controller
@RequestMapping(
        value = "/api/category",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(
            value = "/list",
            method = RequestMethod.GET
    )
    public ResponseEntity loadCategoryList(@RequestParam(value = "lc", required = false, defaultValue = "en") String languageCode) {
        List<Category> categoryList = categoryService.loadCategories(languageCode);
        return ResponseEntity.ok(categoryList);
    }
}
