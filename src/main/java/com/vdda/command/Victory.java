package com.vdda.command;

import com.vdda.command.service.VictoryService;
import com.vdda.slack.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class Victory implements Command {

    private final VictoryService victoryService;

    @Autowired
    public Victory(VictoryService victoryService) {
        this.victoryService = victoryService;
    }

    public String getCommand() {
        return "victory";
    }

    public String getUsage() {
        return "victory @user";
    }

    public String getUsageAdvanced() {
        return getUsage();
    }

    public String getShortDescription() {
        return "Gloat about your well-deserved victory.";
    }

    @Override
    public Response run(Map<String, String> parameters) {

        List<String> args = getArguments(parameters);

        if (args.isEmpty()) {
            return commandUsageResponse();
        }

        victoryService.processRequest(parameters, args);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
