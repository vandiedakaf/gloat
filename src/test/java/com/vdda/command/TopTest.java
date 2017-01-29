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

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "dev")
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

        assertThat(response.getText(), containsString("We're processing your request..."));

    }
}
