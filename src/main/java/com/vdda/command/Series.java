package com.vdda.command;

import com.vdda.command.service.SeriesService;
import com.vdda.slack.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Getter
public class Series implements Command {

    public static final int OUTCOME_ARGUMENT = 1;

    private final String command = "series";
    private final String usage = "series @user (series outcome, e.g. 'wld')";
    private final String usageAdvanced = "series @user (followed by a sequence of characters, either 'w','l' or 'd', denoting 'wins', 'losses' and 'draws'). E.g. 'series @slackbot wwldw' denotes two wins, a loss, a draw and another win against the user @slackbot.";
    private final String shortDescription = "Log the outcome of a series of contests.";

    @Getter(AccessLevel.NONE)
    private final SeriesService seriesService;

    @Autowired
    public Series(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    @Override
    public Response run(Map<String, String> parameters) {

        List<String> args = getArguments(parameters);

        if (!(validateArgs(args))) {
            return commandUsageResponse();
        }

        seriesService.processRequest(parameters, args);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }

    private boolean validateArgs(List<String> args) {
        if (args.size() != 2) {
            return false;
        }

        return args.get(OUTCOME_ARGUMENT).matches("[wldWLD]+");
    }
}
