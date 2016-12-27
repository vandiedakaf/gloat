package com.vdda.controller;

import com.vdda.slack.Response;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by francois
 * on 2016-10-22
 * for vandiedakaf solutions
 */
@RestController
public class GloatController {

    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response gloat(@RequestBody String parameters) {

        System.out.println("GloatController.gloat");
        System.out.println("parameters = " + parameters);

        Response response = new Response();
        response.setText("tada");

        return CommandsHandler.run(parameters);
    }
}
