package com.vdda.command;

import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.tool.Parameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommandsService implements ApplicationContextAware, InitializingBean {

    private Map<String, Command> commands;
    private int maxLen = 0;
    private ApplicationContext applicationContext;
    @Value("${SLACK_TOKEN:SLACK_TOKEN}")
    private String slackToken;

    public Response run(String parametersString) {

        Map<String, String> parametersMap = Parameters.parse(parametersString);

        if (!slackToken.equals(parametersMap.get(SlackParameters.TOKEN.toString()))) {
            log.warn("Incorrect Token");
            throw new IllegalArgumentException("Incorrect Token");
        }

        String text = parametersMap.get(SlackParameters.TEXT.toString());

        if (text != null && !text.isEmpty()) {
            String[] args = text.split("\\s+"); // matches for one or more whitespaces (so that it trims as well)
            if (args.length != 0) {
                Command tool = commands.get(args[0]);
                if (tool != null) {
                    return tool.run(parametersMap);
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder("The available gloat commands are:\n");

        for (Command command : commands.values()) {
            stringBuilder.append(String.format("`%-" + maxLen + "s - %s Usage: %s`\n", command.getCommand(), command.getShortDescription(), command.getUsage()));
        }

        Response response = new Response();
        response.setText(stringBuilder.toString());
        return response;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Command> applicationCommands = applicationContext.getBeansOfType(Command.class);

        commands = toTreeMap(applicationCommands);
        commands.values().forEach(c -> maxLen = Math.max(c.getCommand().length(), maxLen));
    }

    Map<String, Command> toTreeMap(Map<String, Command> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getValue().getCommand(),
                        Map.Entry::getValue,
                        CommandsService::mergeCommandsCollision,
                        TreeMap::new
                ));
    }

    static Command mergeCommandsCollision(Command c1, Command c2) {
        return c2;
    }
}
