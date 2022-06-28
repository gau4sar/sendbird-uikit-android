package com.sendbird.uikit.model;

public class TagInfo {
    private String tagString;
    private String userId;

    public TagInfo(String tagString, String userId) {
        this.tagString = tagString;
        this.userId = userId;
    }

    public String getTagString() {
        return tagString;
    }

    public String getUserId() {
        return userId;
    }
}
