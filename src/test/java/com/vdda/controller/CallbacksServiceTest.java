package com.vdda.controller;

import com.vdda.action.CallbackRequest;
import com.vdda.action.Callbacks;
import com.vdda.action.VictoryConfirm;
import com.vdda.slack.Response;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "dev")
public class CallbacksServiceTest {

    private static final String CALLBACK_ID = Callbacks.VICTORY_CONFIRM.toString() + "|userId";

    @Mocked
    private VictoryConfirm victoryConfirm;

    @Autowired
    private CallbacksService callbacksService;

    @Test
    public void noCallbackRequest() throws Exception {

        Response response = callbacksService.run(null);

        assertThat(response.getText(), containsString("Unknown callback id."));
    }

    @Test
    public void callbackEmpty() throws Exception {

        CallbackRequest callbackRequest = new CallbackRequest();

        Response response = callbacksService.run(callbackRequest);

        assertThat(response.getText(), containsString("Unknown callback id."));
    }

    @Test
    public void callbackIdEmpty() throws Exception {

        CallbackRequest callbackRequest = new CallbackRequest();

        Response response = callbacksService.run(callbackRequest);

        assertThat(response.getText(), containsString("Unknown callback id."));
    }

    @Test
    public void victoryConfirmGolden() throws Exception {

        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setCallbackId(CALLBACK_ID);

        Response response = callbacksService.run(callbackRequest);

        new Verifications() {{
            CallbackRequest callbackRequestCapture;
            victoryConfirm.run(callbackRequestCapture = withCapture());

            assertThat(callbackRequestCapture, is(equalTo(callbackRequest)));
        }};
    }

}