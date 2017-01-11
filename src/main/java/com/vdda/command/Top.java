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
public class Top implements Command {
    @Override
    public Response run(Map<String, String> parameters) {
        Response response = new Response();
        response.setText("COMING SOON.");
        return response;
    }

    @Override
    public String getCommand() {
        return "top";
    }

    @Override
    public String getUsage() {
        return "top";
    }

    @Override
    public String getShortDescription() {
        return "Lists the top ranking players (also known as the winners). Coming soon.";
    }
}
