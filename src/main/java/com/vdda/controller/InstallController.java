package com.vdda.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by francois
 * on 2016-10-22
 * for vandiedakaf solutions
 */
@Controller
@RequestMapping("/install")
@Slf4j
public class InstallController {


    @RequestMapping(method = RequestMethod.GET)
    public String install() {

        return "install";
    }

}
