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
public class Gloat implements Command {
    @Override
    public Response run(Map<String, String> parameters, List<String> args) {
        Response response = new Response();
        response.setText("Oh no, young one, this is reserved for champs only :face_with_head_bandage:");
        // X has won Y of their last Z matches.
        return response;
    }

    @Override
    public String getCommand() {
        return "gloat";
    }

    @Override
    public String getUsage() {
        return "gloat";
    }

    @Override
    public String getShortDescription() {
        return "Let them know who's king of this channel!";
    }
}
