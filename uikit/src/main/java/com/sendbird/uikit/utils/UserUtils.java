package com.sendbird.uikit.utils;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sendbird.android.SendBird;
import com.sendbird.android.User;
import com.sendbird.uikit.R;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.interfaces.UserInfo;

public class UserUtils {
    public static <T extends User> UserInfo toUserInfo(@NonNull T user) {
        return new UserInfo() {
            @Override
            public String getUserId() {
                return user.getUserId();
            }

            @Override
            public String getNickname() {
                return user.getNickname();
            }

            @Override
            public String getProfileUrl() {
                return user.getProfileUrl();
            }

            @Override
            public String getUserPhoneNumber() {
                return user.getMetaData("phone");
            }

            @Override
            public String getUsername() {
                return user.getMetaData("username");
            }
        };
    }

    @NonNull
    public static String getDisplayName(@NonNull Context context, @Nullable User user) {
        String nickname = context.getString(R.string.sb_text_channel_list_title_unknown);
        if (user == null) return nickname;
        String phoneNumber = user.getMetaData("phone");

        User currentUser = SendBird.getCurrentUser();
        if (currentUser != null) {
            if (user.getUserId() != null && user.getUserId().equals(currentUser.getUserId())) {
                nickname = context.getString(R.string.sb_text_you);
            } else if (!TextUtils.isEmpty(phoneNumber)) {
                nickname = SendBirdUIKit.findPhoneBookName(phoneNumber);
            } else if (!TextUtils.isEmpty(user.getNickname())) {
                nickname = user.getNickname();
            }
            return nickname;
        } else {
            return phoneNumber;
        }
    }
}
