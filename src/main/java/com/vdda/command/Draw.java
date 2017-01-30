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
public class Draw implements Command {

    private final String command = "draw";
    private final String usage = "draw @user";
    private final String shortDescription = "No winners here. Coming soon.";

    @Override
    public Response run(Map<String, String> parameters) {
        Response response = new Response();
        response.setText("COMING SOON.");
        return response;
    }
}
