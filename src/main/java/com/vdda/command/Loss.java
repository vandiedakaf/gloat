package com.vdda.command;

import com.vdda.command.service.LossService;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import lombok.AccessLevel;
import lombok.Getter;
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
@Service
@Getter
public class Loss extends Command {

    private final String command = "loss";
    private final String usage = "loss @user";
    private final String shortDescription = "Acknowledge your defeat.";

    @Getter(AccessLevel.NONE)
    private final LossService lossService;

    @Autowired
    public Loss(LossService lossService) {
        this.lossService = lossService;
    }

    @Override
    public Response run(Map<String, String> parameters) {

        List<String> args = getArguments(parameters);

        if (args.isEmpty()) {
            return createUsageResponse();
        }

        // The below is a Future
        lossService.processRequest(parameters, args);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
