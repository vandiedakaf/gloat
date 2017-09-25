package com.vdda.command;

import com.vdda.command.service.StatsService;
import com.vdda.slack.Response;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Getter
public class Stats implements Command {

    private final String command = "stats";
    private final String usage = "stats [@user]";
    private final String usageAdvanced = usage;
    private final String shortDescription = "Returns statistics about yourself (or another user).";

    @Getter(AccessLevel.NONE)
    private final StatsService statsService;

    @Autowired
    public Stats(StatsService statsService) {
        this.statsService = statsService;
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
