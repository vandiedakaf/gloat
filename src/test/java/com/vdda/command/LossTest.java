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
@ActiveProfiles(profiles = "local")
public class LossTest {

    @Autowired
    private Loss loss;

    @Test
    public void missingArgs() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "loss");
        Response response = loss.run(parameters);

        assertThat(response.getText(), containsString("Usage: `loss @user`"));
    }

    @Test
    public void lossGolden() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(SlackParameters.CHANNEL_ID.toString(), "channelId");
        parameters.put(SlackParameters.TEAM_ID.toString(), "teamId");
        parameters.put(SlackParameters.TEXT.toString(), "loss user");

        Response response = loss.run(parameters);
        assertThat(response.getText(), containsString("We're processing your request..."));
    }

    @Test
    public void getCommand() throws Exception {
        assertThat(loss.getCommand(), is(notNullValue()));
    }

    @Test
    public void getUsage() throws Exception {
        assertThat(loss.getUsage(), is(notNullValue()));
    }

    @Test
    public void getShortDescription() throws Exception {
        assertThat(loss.getShortDescription(), is(notNullValue()));
    }
}
