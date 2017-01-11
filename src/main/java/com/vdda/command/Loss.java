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
public class Loss implements Command {
    @Override
    public Response run(Map<String, String> parameters) {
        Response response = new Response();
        response.setText("COMING SOON.");
        return response;
    }

    @Override
    public String getCommand() {
        return "loss";
    }

    @Override
    public String getUsage() {
        return "loss @user";
    }

    @Override
    public String getShortDescription() {
        return "Hey now, at least you can be first at acknowledging defeat. Coming soon.";
    }
}
