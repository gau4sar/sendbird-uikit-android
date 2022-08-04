package com.sendbird.uikit.model;

import androidx.annotation.Keep;

import com.sendbird.android.shadow.com.google.gson.Gson;

@Keep
public class UserConfigInfo {
    private final boolean showLastSeen;
    private final boolean showProfile;
    private final boolean readReceipt;

    public UserConfigInfo(boolean showLastSeen, boolean showProfile, boolean readReceiptDisable) {
        this.showLastSeen = showLastSeen;
        this.showProfile = showProfile;
        this.readReceipt = readReceiptDisable;
    }

    public boolean isShowLastSeen() {
        return showLastSeen;
    }

    public boolean isShowProfile() {
        return showProfile;
    }

    public boolean isReadReceipt() {
        return readReceipt;
    }

    public static UserConfigInfo fromJson(String json) {
        return new Gson().fromJson(json, UserConfigInfo.class);
    }
}
