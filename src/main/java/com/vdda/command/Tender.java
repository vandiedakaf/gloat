package com.vdda.command;

import com.vdda.slack.Response;

import java.util.List;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
public class Tender implements Command {
    @Override
    public Response run(List<String> args) {
        Response response = new Response();
        response.setText("You wot w8?");
        return response;
    }

    @Override
    public String getCommand() {
        return "tender";
    }

    @Override
    public String getUsage() {
        return "tender";
    }

    @Override
    public String getShortDescription() {
        return "Request a challenger.";
    }
}
