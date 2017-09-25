package com.vdda.command;

import com.vdda.command.service.DrawService;
import com.vdda.slack.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class Draw implements Command {

    private final DrawService drawService;

    @Autowired
    public Draw(DrawService drawService) {
        this.drawService = drawService;
    }

    public String getCommand() {
        return "draw";
    }

    public String getUsage() {
        return "draw @user";
    }

    public String getUsageAdvanced() {
        return getUsage();
    }

    public String getShortDescription() {
        return "Declare that there are no winners here.";
    }

    @Override
    public Response run(Map<String, String> parameters) {

        List<String> args = getArguments(parameters);

        if (args.isEmpty()) {
            return commandUsageResponse();
        }

        drawService.processRequest(parameters, args);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
