package com.vdda.callback;

import com.vdda.slack.Response;

/**
 * Created by francois
 * on 2016-12-25
 * for vandiedakaf solutions
 */
public interface Callback {
    Response run(CallbackRequest callbackRequest);

    String getCallbackId();
}
