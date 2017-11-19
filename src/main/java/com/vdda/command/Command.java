package com.vdda.command;

import com.vdda.slack.Response;
import com.vdda.tool.Request;

public interface Command {
    Response run(Request request);

    String getCommand();

    String getUsage();

    String getUsageAdvanced();

    String getShortDescription();

    default Response commandUsageResponse(){
        Response response = new Response();
        response.setText(getShortDescription() + "\nUsage: `" + getUsageAdvanced() + "`");
        return response;
    }
}
