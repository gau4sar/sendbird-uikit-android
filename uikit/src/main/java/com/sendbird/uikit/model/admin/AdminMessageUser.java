package com.sendbird.uikit.model.admin;

import androidx.annotation.Keep;

import com.sendbird.android.shadow.com.google.gson.annotations.SerializedName;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.utils.TextUtils;

@Keep
public class AdminMessageUser {

    @SerializedName("metadata")
    private AdminMessageMetaData metaData;

    public AdminMessageMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(AdminMessageMetaData metaData) {
        this.metaData = metaData;
    }

    public String getName() {
        String phone = metaData.getPhone();

        String phonebookName = SendBirdUIKit.findPhoneBookName(phone);
        return TextUtils.isEmpty(phonebookName) ? phone : phonebookName;
    }
}
