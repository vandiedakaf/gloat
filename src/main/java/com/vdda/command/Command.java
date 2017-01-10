package com.vdda.command;

import com.vdda.slack.Response;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
public interface Command {
    Response run(Map<String, String> parameters);

    // TODO confirm whether this is still required since it's not used in CommandService anymore
    String getCommand();

    String getUsage();

    String getShortDescription();
}
