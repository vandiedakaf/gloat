package com.vdda.controller;

import com.google.gson.Gson;
import com.vdda.callback.CallbackRequest;
import com.vdda.slack.Response;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by francois
 * on 2017-01-05
 * for vandiedakaf solutions
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CallbacksControllerTest {

    @Mocked
    CallbacksService callbacksService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void postEmptyCallback() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/callbacks")
                .content("nil")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void postEmptyJsonParseFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/callbacks")
                .content("payload=[]")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void postCallbackRequestEmpty() throws Exception {
        CallbackRequest[] callbackRequests = new CallbackRequest[0];

        mvc.perform(MockMvcRequestBuilders
                .post("/callbacks")
                .content("payload=" + new Gson().toJson(callbackRequests))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void postGolden() throws Exception {

        new MockUp<System>() {
            @Mock
            public String getenv(final String string) {
                return "SLACK_TOKEN";
            }
        };

        new Expectations() {{
            callbacksService.run(withNotNull());
            result = new Response();
        }};

        mvc.perform(MockMvcRequestBuilders
                .post("/callbacks")
                .content("payload={\"callback_id\":\"victory_confirm|userId\",\"token\":\"SLACK_TOKEN\"}")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void postWrongToken() throws Exception {

        new MockUp<System>() {
            @Mock
            public String getenv(final String string) {
                return "SLACK_TOKEN";
            }
        };

        mvc.perform(MockMvcRequestBuilders
                .post("/callbacks")
                .content("payload={\"callback_id\":\"victory_confirm|userId\",\"token\":\"WRONG_TOKEN\"}")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }
}
