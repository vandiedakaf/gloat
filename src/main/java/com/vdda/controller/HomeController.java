package com.vdda.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
@Slf4j
public class HomeController {

	@RequestMapping(method = RequestMethod.GET)
	public String home() {

		return "home";
	}
}
