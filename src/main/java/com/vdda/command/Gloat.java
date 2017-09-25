package com.vdda.command;

import com.vdda.command.service.GloatService;
import com.vdda.slack.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Gloat implements Command {

    private final GloatService gloatService;

    @Autowired
    public Gloat(GloatService gloatService) {
        this.gloatService = gloatService;
    }

    public String getCommand() {
        return "gloat";
    }

    public String getUsage() {
        return "gloat";
    }

    public String getUsageAdvanced() {
        return getUsage();
    }

    public String getShortDescription() {
        return "Gloat about your reign at the top of the logs.";
    }

    @Override
    public Response run(Map<String, String> parameters) {

        gloatService.processRequest(parameters);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
