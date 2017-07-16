package com.vdda.command;

import com.vdda.command.service.VictoryService;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Slf4j
@Service
@Getter
public class Victory extends Command {

    private final String command = "victory";
    private final String usage = "victory @user";
    private final String shortDescription = "Gloat about your well-deserved victory.";

    @Getter(AccessLevel.NONE)
    private final VictoryService victoryService;

    @Autowired
    public Victory(VictoryService victoryService) {
        this.victoryService = victoryService;
    }

    @Override
    public Response run(Map<String, String> parameters) {

        List<String> args = getArguments(parameters);

        if (args.isEmpty()) {
            Response response = new Response();
            response.setText(getShortDescription() + "\nUsage: `" + getUsage() + "`");
            return response;
        }

        // The below is a Future
        victoryService.processRequest(parameters, args);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
