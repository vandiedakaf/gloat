package com.vdda.command;

import com.vdda.command.service.TenderService;
import com.vdda.slack.Response;
import com.vdda.tool.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Tender implements Command {

    private final TenderService tenderService;

    @Autowired
    public Tender(TenderService tenderService) {
        this.tenderService = tenderService;
    }

    public String getCommand() {
        return "tender";
    }

    public String getUsage() {
        return "tender";
    }

    public String getUsageAdvanced() {
        return getUsage();
    }

    public String getShortDescription() {
        return "Request a challenger!";
    }

    @Override
    public Response run(Request request) {

        tenderService.processRequest(request);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
