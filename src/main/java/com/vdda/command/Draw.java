package com.vdda.command;

import com.vdda.command.service.DrawService;
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
public class Draw implements Command {

    private final String command = "draw";
    private final String usage = "draw @user";
    private final String shortDescription = "No winners here.";

    @Getter(AccessLevel.NONE)
    private final DrawService drawService;

    @Autowired
    public Draw(DrawService drawService) {
        this.drawService = drawService;
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
        drawService.processRequest(parameters, args);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
