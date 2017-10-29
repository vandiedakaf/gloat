package com.vdda.command;

import com.vdda.command.service.SeriesService;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class Series implements Command {

    public static final int OUTCOME_ARGUMENT = 1;

    private final SeriesService seriesService;

    @Autowired
    public Series(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    public String getCommand() {
        return ""; // this command can actually never be met because null and empty checks are utilised
    }

    public String getUsage() {
        return "@user (outcome of contests, e.g. 'wld')";
    }

    public String getUsageAdvanced() {
        return "@user (followed by a sequence of characters, either 'w','l' or 'd', denoting 'wins', 'losses' and 'draws'). E.g. '@slackbot wwldw' denotes two wins, a loss, a draw and another win against the user @slackbot.";
    }

    public String getShortDescription() {
        return "Log the outcome of contests.";
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

    @Override
    public List<String> getArguments(Map<String, String> parameters) {
        String[] argsArray = parameters.get(SlackParameters.TEXT.toString()).split(" ");
        return Arrays.asList(argsArray); // this command class does not have a preceding command string (e.g. "series" or "stats")
    }

    private boolean validateArgs(List<String> args) {
        args.forEach(System.out::println);
        return args.size() == 2 && args.get(OUTCOME_ARGUMENT).matches("[wldWLD]+");

    }
}
