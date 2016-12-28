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
public class Tender implements Command {
    @Override
    public Response run(Map<String, String> parameters, List<String> args) {
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
