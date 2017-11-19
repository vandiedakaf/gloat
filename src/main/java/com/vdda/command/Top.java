package com.vdda.command;

import com.vdda.command.service.TopService;
import com.vdda.slack.Response;
import com.vdda.tool.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Top implements Command {

	private static final int ARGUMENT_OFFSET = 1;
	private final TopService topService;

	@Autowired
	public Top(TopService topService) {
		this.topService = topService;
	}

	public String getCommand() {
		return "top";
	}

	public String getUsage() {
		return "top [number of top players]";
	}

	public String getUsageAdvanced() {
		return getUsage();
	}

	public String getShortDescription() {
		return "Lists the top ranking players.";
	}

	@Override
	public Response run(Request request) {

		List<String> args = request.getArguments(ARGUMENT_OFFSET);

		if (!(validArgs(args))) {
			return commandUsageResponse();
		}

		if (args.isEmpty()) {
			topService.processRequest(request);
		} else {
			topService.processRequest(request, Integer.parseInt(args.get(0)));
		}

		Response response = new Response();
		response.setText("We're processing your request...");
		return response;
	}

	boolean validArgs(List<String> args) {
		// positive integer
		return args.isEmpty() || args.get(0) != null && args.get(0).matches("^[1-9]\\d*");
	}
}
