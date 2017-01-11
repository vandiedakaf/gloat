package com.vdda.command;

import com.vdda.slack.Response;

import java.util.Map;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
public interface Command {
    Response run(Map<String, String> parameters);

    String getCommand();

    String getUsage();

    String getShortDescription();
}
