package com.vdda.controller;

import com.vdda.jpa.Category;
import com.vdda.jpa.User;
import com.vdda.repository.CategoryRepository;
import com.vdda.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

/**
 * Created by francois on 2016-10-22.
 */
@RestController
@Slf4j
public class GloatController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Gloat!";
    }

    @RequestMapping("/gloat/")
    public String gloat() {
        log.debug("GloatController.gloat");

        User user = new User(Long.valueOf(new Random().nextInt(9999)));

        userRepository.save(user);
        return "About?";
    }


    @RequestMapping(value = "/categories/", method = RequestMethod.GET)
    public List<Category> getCategories() {
        log.debug("GloatController.getCategories");

        Category category = new Category(123l);

        return (List<Category>) categoryRepository.findAll();
    }
}
