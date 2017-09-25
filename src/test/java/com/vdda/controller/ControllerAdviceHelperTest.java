package com.vdda.controller;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ControllerAdviceHelperTest {

    @Test
    public void handleBadRequest() throws Exception {
        ControllerAdviceHelper controllerAdviceHelper = new ControllerAdviceHelper();

        HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        controllerAdviceHelper.handleBadRequests(httpServletResponse);

        assertThat(httpServletResponse.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }
}
