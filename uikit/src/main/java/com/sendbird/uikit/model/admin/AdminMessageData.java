package com.sendbird.uikit.model.admin;

import androidx.annotation.Keep;

import com.sendbird.android.shadow.com.google.gson.annotations.SerializedName;
import com.sendbird.uikit.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

@Keep
public class AdminMessageData {
    @SerializedName("type")
    private String type;

    @SerializedName("users")
    private List<AdminMessageUser> users;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AdminMessageUser> getUsers() {
        return users;
    }

    public void setUsers(List<AdminMessageUser> users) {
        this.users = users;
    }

    public String getContent() {
        if (type.equalsIgnoreCase(AdminMessageType.USER_JOINED)) {
            return joinUserNames() + " joined";
        }
        return "";
    }

    public String joinUserNames() {
        List<String> names = getUserNames();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            if (!TextUtils.isEmpty(name)) {
                builder.append(name);
                if (i != names.size() - 1) {
                    builder.append(", ");
                }
            }
        }
        return builder.toString();
    }

    public List<String> getUserNames() {
        List<String> names = new ArrayList<>();
        for (AdminMessageUser user : users) {
            names.add(user.getName());
        }
        return names;
    }
}

