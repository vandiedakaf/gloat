package com.vdda.command;

import com.github.seratch.jslack.api.model.User;
import com.vdda.jpa.Category;
import com.vdda.repository.CategoryRepository;
import com.vdda.slack.Response;
import com.vdda.slack.SlackParameters;
import com.vdda.slack.SlackUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
@Slf4j
@Service
public class Victory implements Command {

    private final CategoryRepository categoryRepository;

    @Autowired
    public Victory(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Response run(Map<String, String> parameters, List<String> args) {

        if (args.isEmpty()) {
            Response response = new Response();
            response.setText(getShortDescription() + "\nUsage: `" + getUsage() + "`");
            return response;
        }

        final String teamId = parameters.get(SlackParameters.TEAM_ID.toString());
        SlackUtilities slackUtilities = new SlackUtilities(teamId);
        Optional<User> user = slackUtilities.getUser(args.get(0));

        if (!user.isPresent()) {
            Response response = new Response();
            response.setText("Sorry, seems like " + args.get(0) + " is some imaginary person.");
            return response;
        }

        final String channelId = parameters.get(SlackParameters.CHANNEL_ID.toString());

        Category category = categoryRepository.findByTeamIdAndChannelId(teamId, channelId);

        if (category == null) {
            category = new Category(teamId, channelId);
            categoryRepository.save(category);
        }

        Response response = new Response();
        response.setText("You beat " + user.get().getName() + ", now did you?");
        // TODO add confirmation buttons
        return response;
    }

    @Override
    public String getCommand() {
        return "victory";
    }

    @Override
    public String getUsage() {
        return "victory @user";
    }

    @Override
    public String getShortDescription() {
        return "Gloat about your well-deserved victory.";
    }
}
