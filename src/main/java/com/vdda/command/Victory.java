package com.vdda.command;

import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Slf4j
@Service
public class Victory implements Command {

    private final VictoryService victoryService;

    @Autowired
    public Victory(VictoryService victoryService) {
        this.victoryService = victoryService;
    }

    @Override
    public Response run(Map<String, String> parameters) {

        String[] argsArray = parameters.get(SlackParameters.TEXT.toString()).split(" ");
        List<String> args = Arrays.asList(argsArray).subList(1, argsArray.length);

        if (args.isEmpty()) {
            Response response = new Response();
            response.setText(getShortDescription() + "\nUsage: `" + getUsage() + "`");
            return response;
        }

        Future<?> futureProcessRequest = victoryService.processRequest(parameters, args);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }

    @Override
    public String getCommand() {
        return "victory";
    }

    @Override
    public String getUsage() {
        return "victory @user";
    }

    @Override
    public String getShortDescription() {
        return "Gloat about your well-deserved victory.";
    }
}
