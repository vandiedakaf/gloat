package com.vdda.command;

import com.vdda.slack.Response;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Service
@Getter
public class Loss implements Command {

    private final String command = "loss";
    private final String usage = "loss @user";
    private final String shortDescription = "Hey now, at least you can be first at acknowledging defeat. Coming soon.";

    @Override
    public Response run(Map<String, String> parameters) {
        Response response = new Response();
        response.setText("COMING SOON.");
        return response;
    }
}
