package com.vdda.controller;

import com.vdda.command.*;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.tool.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Service
public class CommandsHandler {

    private Defeat defeat;
    private Gloat gloat;
    private Top top;
    private Tender tender;
    private Victory victory;
    private Map<String, Command> commands;
    private static int maxLen = 0;

    @Autowired
    void CommandsHandler(Defeat defeat, Gloat gloat, Top top, Tender tender, Victory victory){
        this.defeat = defeat;
        this.gloat = gloat;
        this.top = top;
        this.tender = tender;
        this.victory = victory;

        commands = new TreeMap<>();
        for (Command command : new Command[]{
                this.defeat,
                this.gloat,
                this.top,
                this.tender,
                this.victory
        }) {
            System.out.println("Adding: " + command.getCommand());
            Command prev = commands.put(command.getCommand(), command);
            if (prev != null) {
                throw new AssertionError(
                        "Two commands with identical commands: " + command + ", " + prev);
            }
            maxLen = Math.max(command.getCommand().length(), maxLen);
        }
    }

    Response run(String parametersString) {

        Map<String, String> parametersMap = Parameters.parse(parametersString);

        String text = parametersMap.get(SlackParameters.TEXT.toString());
        System.out.println(text);

        if (text != null && !text.isEmpty()) {
            String[] args = text.split(" ");
            if (args.length != 0) {
                Command tool = commands.get(args[0]);
                if (tool != null) {
                    return tool.run(parametersMap, Arrays.asList(args).subList(1, args.length));
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder("The available gloat commands are:\n");

        for (Command command : commands.values()) {
            stringBuilder.append(String.format("`%" + maxLen + "s - %s Usage: %s`\n", command.getCommand(), command.getShortDescription(), command.getUsage()));
        }

        Response response = new Response();
        response.setText(stringBuilder.toString());
        return response;
    }

}
