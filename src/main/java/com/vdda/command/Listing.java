package com.vdda.command;

import com.vdda.slack.Response;

import java.util.List;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
public class Listing implements Command {
    @Override
    public Response run(List<String> args) {
        Response response = new Response();
        response.setText("1) Someone 2) Someone Else");
        return response;
    }

    @Override
    public String getCommand() {
        return "list";
    }

    @Override
    public String getUsage() {
        return "list";
    }

    @Override
    public String getShortDescription() {
        return "Lists the top ranking players (also known as the winners).";
    }
}
