package com.vdda.controller;

import com.vdda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by francois on 2016-10-22.
 */
@RestController
public class GloatController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Gloat!";
    }
}
