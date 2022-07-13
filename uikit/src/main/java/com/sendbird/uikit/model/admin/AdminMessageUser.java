package com.sendbird.uikit.model.admin;

import androidx.annotation.Keep;

import com.sendbird.android.shadow.com.google.gson.annotations.SerializedName;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.utils.TextUtils;

@Keep
public class AdminMessageUser {

    @SerializedName("metadata")
    private AdminMessageMetaData metaData;

    @SerializedName("user_id")
    private String userId;

    public AdminMessageMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(AdminMessageMetaData metaData) {
        this.metaData = metaData;
    }

    public boolean itsMe() {
        return SendBirdUIKit.isItMe(userId);
    }

    public String getName() {
        boolean itsMe = itsMe();
        if (itsMe) return "You";

        String phone = metaData.getPhone();
        String phonebookName = SendBirdUIKit.findPhoneBookName(phone);
        return TextUtils.isEmpty(phonebookName) ? phone : phonebookName;
    }
}
