package com.vdda.command;

import com.vdda.command.*;
import com.vdda.slack.Response;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.BeforeClass;
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
import static org.hamcrest.Matchers.greaterThan;
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

    @BeforeClass
    public static void applySharedMockups() {
        new MockUp<System>() {
            @Mock
            public String getenv(final String string) {
                return "SLACK_TOKEN";
            }
        };
    }

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
    public void containsText() throws Exception {

        String parameters = "token=SLACK_TOKEN&text=test";

        Response response = commandsService.run(parameters);

        assertThat(response.getText(), containsString("The available gloat commands are"));
    }

    @Test
    public void gloat() throws Exception {

        String parameters = "token=SLACK_TOKEN&text=gloat";

        commandsService.run(parameters);

        new Verifications() {{
            Map<String, String> parametersMap;
            gloat.run(parametersMap = withCapture());

            assertThat(parametersMap.get("text"), is(equalTo("gloat")));
        }};
    }

    @Test
    public void confirmCommandsSort() throws Exception {

        Map<String, Command> unsortedMap = new HashMap<>();

        unsortedMap.put("v", new Victory(null));
        unsortedMap.put("d", new Draw(null));
        unsortedMap.put("l", new Loss(null));

        Map<String, Command> sortedMap = commandsService.toTreeMap(unsortedMap);

        String prevCommand = "";
        for (Command command : sortedMap.values()) {
            assertThat(command.getCommand(), is(greaterThan(prevCommand)));
            prevCommand = command.getCommand();
        }
    }
}
