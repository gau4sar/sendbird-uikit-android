package com.sendbird.uikit.model.admin;

import androidx.annotation.Keep;

import com.sendbird.android.shadow.com.google.gson.annotations.SerializedName;

@Keep
public class AdminChannelChange {
    @SerializedName("new")
    private String newChange;

    @SerializedName("old")
    private String oldChange;

    @SerializedName("key")
    private String key;

    public String getNewChange() {
        return newChange;
    }

    public void setNewChange(String newChange) {
        this.newChange = newChange;
    }

    public String getOldChange() {
        return oldChange;
    }

    public void setOldChange(String oldChange) {
        this.oldChange = oldChange;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
