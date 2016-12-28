package com.vdda.command;

import com.vdda.slack.Response;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Service
public class Defeat implements Command {
    @Override
    public Response run(Map<String, String> parameters, List<String> args) {
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
