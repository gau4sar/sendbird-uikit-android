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

    @SerializedName("changes")
    private List<AdminChannelChange> changes;

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

    public List<AdminChannelChange> getChanges() {
        return changes;
    }

    public void setChanges(List<AdminChannelChange> changes) {
        this.changes = changes;
    }

    public String getContent() {
        if (type.equalsIgnoreCase(AdminMessageType.CHANNEL_CREATE)) {
            return "The conversation is created.";
        } else if (type.equalsIgnoreCase(AdminMessageType.USER_JOINED)) {
            String joinedName = joinUserNames();
            return android.text.TextUtils.isEmpty(joinedName) ? "" : joinedName + " joined";
        } else if (type.equalsIgnoreCase(AdminMessageType.USER_LEAVE)) {
            String joinedName = joinUserNames();
            return android.text.TextUtils.isEmpty(joinedName) ? "" : joinedName + " left";
        } else if (type.equalsIgnoreCase(AdminMessageType.CHANNEL_CHANGE)) {
            if (changes != null && !changes.isEmpty()) {
                return "The "
                        + joinChannelChanges()
                        + " "
                        + (changes.size() == 1 ? "was" : "were")
                        + " updated";
            } else {
                return "";
            }
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

    public String joinChannelChanges() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < changes.size(); i++) {
            AdminChannelChange channelChange = changes.get(i);
            if (!TextUtils.isEmpty(channelChange.getKey())) {
                String key = channelChange.getKey().replace("cover_url", "cover image");
                builder.append(key);
                if (i < changes.size() - 1) {
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

