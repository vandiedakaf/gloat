package com.vdda.command;

import com.vdda.command.service.StatsService;
import com.vdda.slack.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class Stats implements Command {

    private final StatsService statsService;

    @Autowired
    public Stats(StatsService statsService) {
        this.statsService = statsService;
    }

    public String getCommand() {
        return "stats";
    }

    public String getUsage() {
        return "stats [@user]";
    }

    public String getUsageAdvanced() {
        return getUsage();
    }

    public String getShortDescription() {
        return "Returns statistics about yourself (or another user).";
    }

    @Override
    public Response run(Map<String, String> parameters) {

        List<String> args = getArguments(parameters);

        statsService.processRequest(parameters, args);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
