package com.vdda.command;

import com.vdda.command.service.GloatService;
import com.vdda.slack.Response;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Getter
public class Gloat implements Command {

    private final String command = "gloat";
    private final String usage = "gloat";
    private final String shortDescription = "Gloat about your reign at the top of the logs.";

    @Getter(AccessLevel.NONE)
    private final GloatService gloatService;

    @Autowired
    public Gloat(GloatService gloatService) {
        this.gloatService = gloatService;
    }

    @Override
    public Response run(Map<String, String> parameters) {

        gloatService.processRequest(parameters);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
