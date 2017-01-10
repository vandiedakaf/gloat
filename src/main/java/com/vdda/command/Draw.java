package com.vdda.command;

import com.vdda.slack.Response;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Service
public class Draw implements Command {
    @Override
    public Response run(Map<String, String> parameters) {
        Response response = new Response();
        response.setText("COMING SOON.");
        return response;
    }

    @Override
    public String getCommand() {
        return "draw";
    }

    @Override
    public String getUsage() {
        return "draw @user";
    }

    @Override
    public String getShortDescription() {
        return "No winners here. Coming soon.";
    }
}
