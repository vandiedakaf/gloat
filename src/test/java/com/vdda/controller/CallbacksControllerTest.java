package com.vdda.controller;

import com.google.gson.Gson;
import com.vdda.action.CallbackRequest;
import com.vdda.slack.Response;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles(profiles = "dev")
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

        new Expectations() {{
            callbacksService.run(withNotNull());
            result = new Response();
        }};

        mvc.perform(MockMvcRequestBuilders
                .post("/callbacks")
                .content("payload={\"callback_id\":\"victory_confirm|userId\"}")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}
