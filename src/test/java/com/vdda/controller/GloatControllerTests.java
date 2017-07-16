package com.vdda.controller;

import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GloatControllerTests {

    @Tested
    private GloatController gloatController;

    @Injectable
    private CommandsService commandsService;

    @Test
    public void incorrectToken() throws Exception {

        String parameters = "parameters";
        gloatController.gloat(parameters);

        new Verifications() {{
            String parametersSent;
            commandsService.run(parametersSent = withCapture());

            assertThat(parametersSent, is(parameters));
        }};
    }

}
