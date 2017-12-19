package com.vdda.command;

import com.vdda.command.service.StatsService;
import com.vdda.slack.Response;
import com.vdda.tool.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Stats implements Command {

	private final StatsService statsService;

	@Autowired
	public Stats(StatsService statsService) {
		this.statsService = statsService;
	}

	public String getCommand() {
		return "stats";
	}

	public String getUsage() {
		return "stats [@user]";
	}

	public String getUsageAdvanced() {
		return getUsage();
	}

	public String getShortDescription() {
		return "Returns statistics about the channel and yourself (or another user).";
	}

	@Override
	public Response run(Request request) {
		statsService.processRequest(request);

		Response response = new Response();
		response.setText("We're processing your request...");
		return response;
	}
}
