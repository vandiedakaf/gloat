package com.vdda.controller;

import com.vdda.command.Gloat;
import com.vdda.slack.Response;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2016-12-26
 * for vandiedakaf solutions
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "dev")
public class CommandsServiceTest {

    @Mocked
    private Gloat gloat;

    @Autowired
    private CommandsService commandsHandler;

    @Test
    public void noCommand() throws Exception {

        String parameters = "";

        Response response = commandsHandler.run(parameters);

        assertThat(response.getText(), containsString("The available gloat commands are"));
    }

    @Test
    public void textEmpty() throws Exception {

        String parameters = "text=";

        Response response = commandsHandler.run(parameters);

        assertThat(response.getText(), containsString("The available gloat commands are"));
    }

    @Test
    public void containsText() throws Exception {

        String parameters = "text=test";

        Response response = commandsHandler.run(parameters);

        assertThat(response.getText(), containsString("The available gloat commands are"));
    }

    @Test
    public void gloat() throws Exception {

        String parameters = "text=gloat";

        commandsHandler.run(parameters);

        new Verifications() {{
            Map<String, String> parametersMap;
            gloat.run(parametersMap = withCapture());

            assertThat(parametersMap.get("text"), is(equalTo("gloat")));
        }};
    }
}
