package com.vdda.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.vdda.action.CallbackRequest;
import com.vdda.slack.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.method.P;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by francois
 * on 2016-10-22
 * for vandiedakaf solutions
 */
@RestController
@RequestMapping("/callbacks")
@Slf4j
public class CallbacksController {

    @Autowired
    private CallbacksService callbacksService;

    private static final String PAYLOAD = "payload";
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Response processCallback(@RequestBody MultiValueMap multiValueMap) {

        // TODO confirm token validity


        if (multiValueMap.get(PAYLOAD) == null){
            log.warn("Empty Payload");
            throw new IllegalArgumentException("Empty Payload");
        }

        log.debug(multiValueMap.get(PAYLOAD).toString());

        CallbackRequest[] callbackRequests;
        try {
            callbackRequests = GSON.fromJson(multiValueMap.get(PAYLOAD).toString(), CallbackRequest[].class);
        } catch (JsonSyntaxException e) {
            log.warn("Could not parse Payload");
            throw new IllegalArgumentException("Could not parse Payload");
        }

        if (callbackRequests.length == 0) {
            log.warn("No Callback Request");
            throw new IllegalArgumentException("No Callback Request");
        }

        return callbacksService.run(callbackRequests[0]);
    }
}
