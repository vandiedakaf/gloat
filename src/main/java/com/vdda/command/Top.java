package com.vdda.command;

import com.vdda.command.service.TopService;
import com.vdda.slack.Response;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Getter
public class Top implements Command {

    private final String command = "top";
    private final String usage = "top [number of top players]";
    private final String usageAdvanced = usage;
    private final String shortDescription = "Lists the top ranking players.";

    @Getter(AccessLevel.NONE)
    private final TopService topService;

    @Autowired
    public Top(TopService topService) {
        this.topService = topService;
    }

    @Override
    public Response run(Map<String, String> parameters) {

        List<String> args = getArguments(parameters);

        if (!(validateArgs(args))) {
            return commandUsageResponse();
        }

        if (args.isEmpty()) {
            topService.processRequest(parameters);
        } else {
            topService.processRequest(parameters, Integer.parseInt(args.get(0)));
        }

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }

    boolean validateArgs(List<String> args) {
        if (!args.isEmpty()) {
            return args.get(0) != null && args.get(0).matches("^[1-9]\\d*"); // positive integer
        }

        return true;
    }
}
