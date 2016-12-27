package com.vdda.command;

import com.vdda.slack.Response;

import java.util.List;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
public class Victory implements Command {
    @Override
    public Response run(List<String> args) {
        Response response = new Response();
        response.setText("Oh did you?");
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
