package com.vdda.command;

import com.vdda.command.service.SeriesService;
import com.vdda.slack.Response;
import com.vdda.tool.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Series implements Command {

    public static final int OUTCOME_ARGUMENT = 1;

	private final SeriesService seriesService;

    @Autowired
    public Series(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    public String getCommand() {
        return "";
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
    public Response run(Request request) {

		List<String> args = request.getArguments();

		if (!(validateArgs(args))) {
			return commandUsageResponse();
		}

        seriesService.processRequest(request);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }

    private boolean validateArgs(List<String> args) {
        return args.size() == 2 && args.get(OUTCOME_ARGUMENT).matches("[wldWLD]+");

    }
}
