package com.sendbird.uikit.model;

import androidx.annotation.Keep;

import com.sendbird.android.shadow.com.google.gson.Gson;

@Keep
public class UserConfigInfo {
    private final boolean lastSeenDisable;
    private final boolean profileImageDisable;
    private final boolean readReceiptDisable;

    public UserConfigInfo(boolean lastSeenDisable, boolean profileImageDisable, boolean readReceiptDisable) {
        this.lastSeenDisable = lastSeenDisable;
        this.profileImageDisable = profileImageDisable;
        this.readReceiptDisable = readReceiptDisable;
    }

    public boolean isLastSeenDisable() {
        return lastSeenDisable;
    }

    public boolean isProfileImageDisable() {
        return profileImageDisable;
    }

    public boolean isReadReceiptDisable() {
        return readReceiptDisable;
    }

    public static UserConfigInfo fromJson(String json) {
        return new Gson().fromJson(json, UserConfigInfo.class);
    }
}
