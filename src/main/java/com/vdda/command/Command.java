package com.vdda.command;

import com.vdda.slack.Response;

import java.util.List;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
public interface Command {
    Response run(List<String> args);

    String getCommand();

    String getUsage();

    String getShortDescription();
}
