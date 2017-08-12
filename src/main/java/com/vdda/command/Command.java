package com.vdda.command;

import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Command {
    abstract public Response run(Map<String, String> parameters);

    abstract public String getCommand();

    abstract public String getUsage();

    abstract public String getShortDescription();

    List<String> getArguments(Map<String, String> parameters) {
        String[] argsArray = parameters.get(SlackParameters.TEXT.toString()).split(" ");
        return Arrays.asList(argsArray).subList(1, argsArray.length);
    }

    Response createUsageResponse(){
        Response response = new Response();
        response.setText(getShortDescription() + "\nUsage: `" + getUsage() + "`");
        return response;
    }
}
