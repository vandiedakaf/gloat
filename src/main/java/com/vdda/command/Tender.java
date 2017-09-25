package com.vdda.command;

import com.vdda.command.service.TenderService;
import com.vdda.slack.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
    public Response run(Map<String, String> parameters) {

        tenderService.processRequest(parameters);

        Response response = new Response();
        response.setText("We're processing your request...");
        return response;
    }
}
