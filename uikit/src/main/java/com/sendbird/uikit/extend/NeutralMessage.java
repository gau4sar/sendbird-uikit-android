package com.sendbird.uikit.extend;

import com.sendbird.android.BaseMessage;
import com.sendbird.android.shadow.com.google.gson.JsonElement;

public class NeutralMessage extends BaseMessage {

    public NeutralMessage(String channelUrl, long msgId, long createdAt) {
        super(channelUrl, msgId, createdAt);
    }

    protected NeutralMessage(JsonElement el) {
        super(el);
    }

    @Override
    public String getRequestId() {
        return null;
    }
}
