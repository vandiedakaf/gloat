package com.vdda.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/install")
@Slf4j
public class InstallController {

    @Value("${SLACK_CLIENT_ID:slack-client-id-not-set}")
    private String slackClientId;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView install() {

        ModelAndView mav = new ModelAndView("install");
        mav.addObject("slackClientId", slackClientId);
        return mav;
    }

}
