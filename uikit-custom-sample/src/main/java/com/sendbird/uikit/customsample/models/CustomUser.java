package com.sendbird.uikit.customsample.models;

import com.sendbird.android.User;
import com.sendbird.uikit.interfaces.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class CustomUser implements UserInfo {
    User user;

    public CustomUser(User user) {
        this.user = user;
    }

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
    public List<String> getTranslationTargetLanguages() {
        List<String> translationTargetLanguages = new ArrayList<>();
        translationTargetLanguages.add("uk");
        return translationTargetLanguages;
    }

    @Override
    public String getPreferTranslateLanguage() {
        return "uk";
    }

    @Override
    public String getUsername() {
        return user.getMetaData("username");
    }
}
