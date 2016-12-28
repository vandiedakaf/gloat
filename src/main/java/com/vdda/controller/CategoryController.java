package com.vdda.controller;

import com.vdda.jpa.Category;
import com.vdda.mapper.OrikaBeanMapper;
import com.vdda.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by francois
 * on 2016-10-22
 * for vandiedakaf solutions
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    // TODO: probably have to remove this class

    private CategoryRepository categoryRepository;

    private OrikaBeanMapper mapper;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository, OrikaBeanMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Category> getCategories() {

        return (List<Category>) categoryRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Category getCategory(@PathVariable Long id) {

        return categoryRepository.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Category createCategory(@RequestBody Category category) {

        return categoryRepository.save(category);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {

        Category categoryPersisted = categoryRepository.findOne(id);

        if (categoryPersisted == null) {
            throw new IllegalArgumentException("Required path variable 'id' missing");
        }

        // map
        mapper.map(category, categoryPersisted);

        // make sure the id was not overridden
        categoryPersisted.setId(id);

        return categoryRepository.save(categoryPersisted);
    }
}
