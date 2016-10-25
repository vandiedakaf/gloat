package com.vdda.controller;

import com.vdda.jpa.Category;
import com.vdda.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

        return categoryRepository.save(category);
    }
}
