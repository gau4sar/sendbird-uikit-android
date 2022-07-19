package com.sendbird.uikit.model;

import com.sendbird.android.BaseMessageParams;

public class TagInfo {
    private final String tagString;
    private final String tagId;
    private final BaseMessageParams.MentionType mentionType;

    public TagInfo(String tagString, String userId, BaseMessageParams.MentionType mentionType) {
        this.tagString = tagString;
        this.tagId = userId;
        this.mentionType = mentionType;
    }

    public String getTagString() {
        return tagString;
    }

    public String getTagId() {
        return tagId;
    }

    public BaseMessageParams.MentionType getMentionType() {
        return mentionType;
    }
}
