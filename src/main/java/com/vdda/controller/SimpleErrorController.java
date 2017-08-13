package com.vdda.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/error")
@Slf4j
public class SimpleErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @Autowired
    public SimpleErrorController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping
    public String error(HttpServletRequest request) {
        // TODO make custom error page
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        log.debug("ErrorController triggered {}", errorAttributes.getErrorAttributes(requestAttributes, true).toString());
        return "error";
    }
}
