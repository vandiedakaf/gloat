package com.vdda.command;

import com.vdda.slack.SlackParameters;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CommandTest {

    @Test
    public void validateArgs() throws Exception {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.TEXT.toString(), "test arg1 arg2");

        List<String> arguments = (new Gloat(null)).getArguments(parameters);

        assertThat(arguments.size(), is(equalTo(2)));
        assertThat(arguments.get(0), is(equalTo("arg1")));
        assertThat(arguments.get(1), is(equalTo("arg2")));
    }
}