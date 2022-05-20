package com.sendbird.uikit.model.admin;

import androidx.annotation.Keep;

import com.sendbird.android.shadow.com.google.gson.annotations.SerializedName;

@Keep
public class AdminMessageMetaData {

    @SerializedName("username")
    private String username;

    @SerializedName("phone")
    private String phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
