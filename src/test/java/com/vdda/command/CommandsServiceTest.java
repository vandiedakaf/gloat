package com.vdda.command;

import com.vdda.slack.Response;
import com.vdda.tool.Request;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommandsServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mocked
    private Gloat gloat;

    @Autowired
    private CommandsService commandsService;

    @Test
    public void noToken() throws Exception {

        thrown.expect(IllegalArgumentException.class);

        String parameters = "";

        Response response = commandsService.run(parameters);

        assertThat(response.getText(), containsString("The available gloat commands are"));
    }

    @Test
    public void incorrectToken() throws Exception {

        thrown.expect(IllegalArgumentException.class);

        String parameters = "token=NOT_A_TOKEN";

        Response response = commandsService.run(parameters);

        assertThat(response.getText(), containsString("The available gloat commands are"));
    }

    @Test
    public void noCommand() throws Exception {

        String parameters = "token=SLACK_TOKEN";

        Response response = commandsService.run(parameters);

        assertThat(response.getText(), containsString("The available gloat commands are"));
    }

    @Test
    public void textEmpty() throws Exception {

        String parameters = "token=SLACK_TOKEN&text=";

        Response response = commandsService.run(parameters);

        assertThat(response.getText(), containsString("The available gloat commands are"));
    }

    @Test
    public void noCommandSeries() throws Exception {

        String parameters = "token=SLACK_TOKEN&text=@user";

        Response response = commandsService.run(parameters);

        assertThat(response.getText(), containsString("Log the outcome of contests."));
    }

    @Test
    public void gloat() throws Exception {

        String parameters = "token=SLACK_TOKEN&text=gloat";

        commandsService.run(parameters);

        new Verifications() {{
            Request requestVerify;
            gloat.run(requestVerify = withCapture());

            assertThat(requestVerify.getParameter("text"), is(("gloat")));
        }};
    }

    @Test
    public void confirmCommandsSort() throws Exception {

        Map<String, Command> unsortedMap = new HashMap<>();

        unsortedMap.put("stats", new Stats(null));
        unsortedMap.put("tender", new Tender(null));
        unsortedMap.put("series", new Series(null));

        Map<String, Command> sortedMap = commandsService.toTreeMap(unsortedMap);

        String prevCommand = "";
        for (Command command : sortedMap.values()) {
            assertThat(command.getCommand(), is(greaterThanOrEqualTo(prevCommand)));
            prevCommand = command.getCommand();
        }
    }

    @Test
    public void mergeTest() throws Exception {
        Command command = CommandsService.mergeCommandsCollision(new Gloat(null), new Series(null));
        assertThat(command, instanceOf(Series.class));
    }
}
