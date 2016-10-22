package com.vdda.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by francois on 2016-10-22.
 */
@RestController
public class GloatController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Gloat!";
    }
}
