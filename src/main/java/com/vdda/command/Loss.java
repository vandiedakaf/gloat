package com.vdda.command;

import com.vdda.command.service.LossService;
import com.vdda.slack.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class Loss implements Command {

    private final LossService lossService;

    @Autowired
    public Loss(LossService lossService) {
        this.lossService = lossService;
    }

    public String getCommand() {
        return "loss";
    }

    public String getUsage() {
        return "loss @user";
    }

    public String getUsageAdvanced() {
        return getUsage();
    }

    public String getShortDescription() {
        return "Acknowledge your defeat.";
    }

    @Override
    public Response run(Map<String, String> parameters) {

        List<String> args = getArguments(parameters);

        if (args.isEmpty()) {
            return commandUsageResponse();
        }

        lossService.processRequest(parameters, args);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
