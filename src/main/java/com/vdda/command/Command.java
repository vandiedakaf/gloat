package com.vdda.command;

import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface Command {
    Response run(Map<String, String> parameters);

    String getCommand();

    String getUsage();

    String getUsageAdvanced();

    String getShortDescription();

    default List<String> getArguments(Map<String, String> parameters) {
        String[] argsArray = parameters.get(SlackParameters.TEXT.toString()).split(" ");
        return Arrays.asList(argsArray).subList(1, argsArray.length);
    }

    default Response commandUsageResponse(){
        Response response = new Response();
        response.setText(getShortDescription() + "\nUsage: `" + getUsageAdvanced() + "`");
        return response;
    }
}
