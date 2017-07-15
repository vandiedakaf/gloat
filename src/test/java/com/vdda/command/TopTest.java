package com.vdda.command;

import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//@ActiveProfiles(profiles = "local")
public class TopTest {

    @Autowired
    Top top;

    @Test
    public void runGolden() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "top");

        Response response = top.run(parameters);

        // TODO: mock test if topService.processRequest(parameters) is called (also do this for other Commands
//        assertThat(response.getText(), containsString("We're processing your request..."));

    }

    @Test
    public void getCommand() throws Exception {
        assertThat(top.getCommand(), is(notNullValue()));
    }

    @Test
    public void getUsage() throws Exception {
        assertThat(top.getUsage(), is(notNullValue()));
    }

    @Test
    public void getShortDescription() throws Exception {
        assertThat(top.getShortDescription(), is(notNullValue()));
    }
}
