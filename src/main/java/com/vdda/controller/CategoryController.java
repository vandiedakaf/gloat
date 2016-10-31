package com.vdda.controller;

import com.vdda.jpa.Category;
import com.vdda.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by francois on 2016-10-22.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;

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
        System.out.println("CategoryController.createCategory");

        return categoryRepository.save(category);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) throws Exception {
        System.out.println("CategoryController.updateCategory");

        Category categoryPersisted = categoryRepository.findOne(id);

        if (categoryPersisted == null) {
            throw new IllegalArgumentException("Required path variable 'id' missing");
        }

        category.setId(categoryPersisted.getId());

        return categoryRepository.save(category);
    }
}
