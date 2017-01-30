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
public class Tender implements Command {

    private final String command = "tender";
    private final String usage = "tender";
    private final String shortDescription = "Request a challenger. Coming soon.";

    @Override
    public Response run(Map<String, String> parameters) {
        Response response = new Response();
        response.setText("COMING SOON.");
        return response;
    }
}
