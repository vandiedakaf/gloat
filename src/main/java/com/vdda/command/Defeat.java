package com.vdda.command;

import com.vdda.slack.Response;

import java.util.List;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
public class Defeat implements Command {
    @Override
    public Response run(List<String> args) {
        Response response = new Response();
        response.setText("Oh did you?");
        return response;
    }

    @Override
    public String getCommand() {
        return "defeat";
    }

    @Override
    public String getUsage() {
        return "defeat @user";
    }

    @Override
    public String getShortDescription() {
        return "At least acknowledging your defeat is something you can be first at.";
    }
}
