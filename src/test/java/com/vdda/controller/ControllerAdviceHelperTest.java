package com.vdda.controller;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * Created by francois on 2016-10-31.
 */
public class ControllerAdviceHelperTest {

    @Test
    public void handleBadRequest() throws Exception {
        ControllerAdviceHelper controllerAdviceHelper = new ControllerAdviceHelper();

        HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        controllerAdviceHelper.handleBadRequests(httpServletResponse);

        assertThat(httpServletResponse.getStatus(), equalTo(HttpStatus.BAD_REQUEST.value()));
    }
}
