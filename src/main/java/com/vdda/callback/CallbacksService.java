package com.vdda.callback;

import com.vdda.slack.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CallbacksService implements ApplicationContextAware, InitializingBean {

    private Map<String, Callback> callbacks;
    private ApplicationContext applicationContext;

    public Response run(CallbackRequest callbackRequest) {

        if (callbackRequest != null && callbackRequest.getCallbackId() != null) {
            String callbackId = callbackRequest.getCallbackId().split("\\|")[0];

            if (callbackId != null && !callbackId.isEmpty()) {
                Callback callback = callbacks.get(callbackId);
                if (callback != null) {
                    return callback.run(callbackRequest);
                }
            }
        }

        log.debug("Unknown callback id.");
        Response response = new Response();
        response.setText("Unknown callback id.");
        return response;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Callback> callbacksByName = applicationContext.getBeansOfType(Callback.class);
        callbacks = callbacksByName
                .entrySet()
                .stream()
                .collect(Collectors.toMap(s -> s.getValue().getCallbackId(), Map.Entry::getValue, (a, b) -> a, HashMap::new));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
