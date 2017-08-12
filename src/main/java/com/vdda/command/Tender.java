package com.vdda.command;

import com.vdda.command.service.TenderService;
import com.vdda.slack.Response;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Getter
public class Tender extends Command {

    private final String command = "tender";
    private final String usage = "tender";
    private final String shortDescription = "Request a challenger!";

    @Getter(AccessLevel.NONE)
    private final TenderService tenderService;

    @Autowired
    public Tender(TenderService tenderService) {
        this.tenderService = tenderService;
    }

    @Override
    public Response run(Map<String, String> parameters) {

        tenderService.processRequest(parameters);

        // TODO make this async

        return null;
    }
}
