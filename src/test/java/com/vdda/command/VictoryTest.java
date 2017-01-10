package com.vdda.command;

import com.vdda.slack.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

/**
 * Created by francois
 * on 2016-12-28
 * for vandiedakaf solutions
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "dev")
public class VictoryTest {

    @Autowired
    Victory victory;

    @Test
    public void missingArgs() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("channel_id", "channelId");
        parameters.put("team_id", "teamId");
        parameters.put("text", "victory");
        Response response = victory.run(parameters);

        assertThat(response.getText(), containsString("Usage: `victory @user`"));
    }

    @Test
    public void slackbot() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("channel_id", "channelId");
        parameters.put("team_id", "teamId");
        parameters.put("text", "victory @slackbot");

        Response response = victory.run(parameters);
        assertThat(response.getText(), containsString("We're processing your request..."));
    }

    @Test
    public void nobot() throws Exception {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("channel_id", "channelId");
        parameters.put("team_id", "teamId");
        parameters.put("text", "victory this_user_should_not_exist");
        Response response = victory.run(parameters);

        assertThat(response.getText(), containsString("We're processing your request..."));
        // TODO verification

    }
}
