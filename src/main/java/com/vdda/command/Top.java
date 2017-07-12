package com.vdda.command;

import com.vdda.slack.Response;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Service
@Getter
public class Top implements Command {

    private final String command = "top";
    private final String usage = "top";
    private final String shortDescription = "Lists the top ranking players.";

    @Getter(AccessLevel.NONE)
    private final TopService topService;

    @Autowired
    public Top(TopService topService) {
        this.topService = topService;
    }

    @Override
    public Response run(Map<String, String> parameters) {

        topService.processRequest(parameters);

        return null;
    }
}
