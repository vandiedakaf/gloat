package com.vdda.callback;

import com.vdda.slack.Response;

public interface Callback {
    Response run(CallbackRequest callbackRequest);

    String getCallbackId();
}
