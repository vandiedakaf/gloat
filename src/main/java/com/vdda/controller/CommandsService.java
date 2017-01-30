package com.vdda.controller;

import com.vdda.command.Command;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.tool.Parameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Slf4j
@Service
public class CommandsService implements ApplicationContextAware, InitializingBean {

    private Map<String, Command> commands;
    private int maxLen = 0;
    private ApplicationContext applicationContext;

    Response run(String parametersString) {

        Map<String, String> parametersMap = Parameters.parse(parametersString);

        if (!System.getenv("SLACK_TOKEN").equals(parametersMap.get(SlackParameters.TOKEN.toString()))) {
            log.warn("Incorrect Token");
            throw new IllegalArgumentException("Incorrect Token");
        }

        String text = parametersMap.get(SlackParameters.TEXT.toString());

        if (text != null && !text.isEmpty()) {
            String[] args = text.split(" ");
            if (args.length != 0) {
                Command tool = commands.get(args[0]);
                if (tool != null) {
                    return tool.run(parametersMap);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        commands = applicationContext.getBeansOfType(Command.class);
        commands.values().forEach(c -> maxLen = Math.max(c.getCommand().length(), maxLen));
    }
}
