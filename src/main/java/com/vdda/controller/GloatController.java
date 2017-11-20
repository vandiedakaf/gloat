package com.vdda.controller;

import com.vdda.command.CommandsService;
import com.vdda.slack.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GloatController {

    private final CommandsService commandsService;

    @Autowired
    public GloatController(CommandsService commandsService) {
        this.commandsService = commandsService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response gloat(@RequestBody String parameters) {

        log.debug("gloat: {}", parameters);

        return commandsService.run(parameters);
    }
}
