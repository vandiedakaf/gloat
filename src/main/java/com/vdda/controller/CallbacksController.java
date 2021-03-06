package com.vdda.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.vdda.callback.CallbackRequest;
import com.vdda.callback.CallbacksService;
import com.vdda.slack.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/callbacks")
@Slf4j
public class CallbacksController {

	private static final String PAYLOAD = "payload";
	private static final Gson GSON = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();

	private final CallbacksService callbacksService;

	@Value("${SLACK_TOKEN:SLACK_TOKEN}")
	private String slackToken;

	@Autowired
	public CallbacksController(CallbacksService callbacksService) {
		this.callbacksService = callbacksService;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public Response processCallback(@RequestBody MultiValueMap multiValueMap) {

		log.debug("processCallback: {}", multiValueMap);

		if (multiValueMap.get(PAYLOAD) == null) {
			log.warn("Empty Payload");
			throw new IllegalArgumentException("Empty Payload");
		}

		CallbackRequest[] callbackRequests;
		try {
			callbackRequests = GSON.fromJson(multiValueMap.get(PAYLOAD).toString(), CallbackRequest[].class);
		} catch (JsonSyntaxException e) {
			log.warn("Could not parse Payload", e);
			throw new IllegalArgumentException("Could not parse Payload");
		}

		if (!slackToken.equals(callbackRequests[0].getToken())) {
			log.warn("Incorrect Token");
			throw new IllegalArgumentException("Incorrect Token");
		}

		return callbacksService.run(callbackRequests[0]);
	}
}
