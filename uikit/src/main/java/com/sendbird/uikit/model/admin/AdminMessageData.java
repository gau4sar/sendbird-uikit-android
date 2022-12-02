package com.sendbird.uikit.model.admin;

import android.content.Context;

import androidx.annotation.Keep;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.shadow.com.google.gson.annotations.SerializedName;
import com.sendbird.uikit.R;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

@Keep
public class AdminMessageData {
    @SerializedName("type")
    private String type;

    @SerializedName("reason")
    private String reason;

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

    public String getContent(Context context, BaseChannel channel) {
        if (type.equalsIgnoreCase(AdminMessageType.CHANNEL_CREATE)) {
            if (channel instanceof GroupChannel) {
                GroupChannel groupChannel = (GroupChannel) channel;
                if (groupChannel.isSuper()) return context.getString(R.string.channel_is_created);
                else return context.getString(R.string.group_is_created);
            }
        } else if (type.equalsIgnoreCase(AdminMessageType.USER_JOINED)) {

            String joinedName = joinUserNames(context);

            if (channel instanceof GroupChannel) {
                GroupChannel groupChannel = (GroupChannel) channel;
                if (groupChannel.getMemberCount() > 1)
                    return String.format(context.getString(R.string.user_are_added), android.text.TextUtils.isEmpty(joinedName) ? "" : joinedName);
                else
                    return String.format(context.getString(R.string.user_is_added), android.text.TextUtils.isEmpty(joinedName) ? "" : joinedName);
            }

            return String.format(context.getString(R.string.user_joined), android.text.TextUtils.isEmpty(joinedName) ? "" : joinedName);
        } else if (type.equalsIgnoreCase(AdminMessageType.USER_LEAVE)) {
            String joinedName = joinUserNames(context);
            return String.format(context.getString(R.string.user_left), android.text.TextUtils.isEmpty(joinedName) ? "" : joinedName);
        } else if (type.equalsIgnoreCase(AdminMessageType.CHANNEL_CHANGE)) {
            if (changes != null && !changes.isEmpty()) {
                if (changes.size() == 1) {
                    return String.format(context.getString(R.string.channel_was_updated), joinChannelChanges());
                } else {
                    return String.format(context.getString(R.string.channel_were_updated), joinChannelChanges());
                }
            } else {
                return "";
            }
        } else if (type.equalsIgnoreCase(AdminMessageType.USER_ROLE_CHANGE)) {
            if (AdminMessageReason.USER_TO_OPERATOR.equalsIgnoreCase(reason)) {
                boolean onlyMe = users.size() == 1 && users.get(0).itsMe();
                if ((users.size() > 1 || onlyMe)) {

                    if (channel instanceof GroupChannel) {
                        GroupChannel groupChannel = (GroupChannel) channel;
                        if (groupChannel.isSuper())
                            return String.format(context.getString(R.string.user_are_now_operator), joinUserNames(context));
                        else
                            return String.format(context.getString(R.string.user_are_now_admin), joinUserNames(context));
                    }
                } else {
                    if (channel instanceof GroupChannel) {
                        GroupChannel groupChannel = (GroupChannel) channel;
                        if (groupChannel.isSuper())
                            return String.format(context.getString(R.string.user_is_now_operator), joinUserNames(context));
                        else
                            return String.format(context.getString(R.string.user_is_now_admin), joinUserNames(context));
                    }
                }
            }
        }
        return "";
    }

    public String joinUserNames(Context context) {
        List<String> names = getUserNames(context);
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

    public List<String> getUserNames(Context context) {
        List<String> names = new ArrayList<>();
        for (AdminMessageUser user : users) {
            if (user.itsMe()) {
                names.add(context.getString(R.string.sb_text_you));
            } else {
                names.add(user.getName());
            }
        }
        return names;
    }
}

