package com.vdda.controller;

import com.vdda.command.*;
import com.vdda.slack.Response;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2016-12-26
 * for vandiedakaf solutions
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "dev")
public class CommandsHandlerTest {

    @Autowired
    private CommandsHandler commandsHandler;


    @Test
    public void noCommand() throws Exception {

        String parameters = "";

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
    public void defeat(@Mocked Defeat defeat) throws Exception {

        String parameters = "text=defeat";

        commandsHandler.run(parameters);

        new Verifications() {{
            defeat.run(withNotNull(), withNotNull());
        }};
    }

    @Test
    public void gloat(@Mocked Gloat gloat) throws Exception {

        String parameters = "text=gloat";

        commandsHandler.run(parameters);

        new Verifications() {{
            gloat.run(withNotNull(), withNotNull());
        }};
    }

    @Test
    public void gloat(@Mocked Top top) throws Exception {

        String parameters = "text=top";

        commandsHandler.run(parameters);

        new Verifications() {{
            top.run(withNotNull(), withNotNull());
        }};
    }

    @Test
    public void gloat(@Mocked Tender tender) throws Exception {

        String parameters = "text=tender";

        commandsHandler.run(parameters);

        new Verifications() {{
            tender.run(withNotNull(), withNotNull());
        }};
    }

    @Test
    public void gloat(@Mocked Victory victory) throws Exception {

        String parameters = "text=victory";

        commandsHandler.run(parameters);

        new Verifications() {{
            victory.run(withNotNull(), withNotNull());
        }};
    }
}
