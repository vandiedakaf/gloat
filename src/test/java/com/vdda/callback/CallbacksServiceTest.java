package com.vdda.callback;

import com.vdda.callback.CallbackRequest;
import com.vdda.callback.Callbacks;
import com.vdda.callback.CallbacksService;
import com.vdda.callback.ConfirmContestVictory;
import com.vdda.slack.Response;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CallbacksServiceTest {

    private static final String CALLBACK_ID = Callbacks.CONFIRM_VICTORY.toString() + "|userId";

    @Mocked
    private ConfirmContestVictory confirmVictory;

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
            confirmVictory.run(callbackRequestCapture = withCapture());

            assertThat(callbackRequestCapture, is((callbackRequest)));
        }};
    }

}
