package com.vdda.command;

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
public class Loss implements Command {

    private final String command = "loss";
    private final String usage = "loss @user";
    private final String shortDescription = "Hey now, at least you can be first at acknowledging defeat.";

    @Getter(AccessLevel.NONE)
    private final LossService lossService;

    @Autowired
    public Loss(LossService lossService) {
        this.lossService = lossService;
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

        // The below is a Future
        lossService.processRequest(parameters, args);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
