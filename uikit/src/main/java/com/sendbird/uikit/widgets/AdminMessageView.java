package com.sendbird.uikit.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.shadow.com.google.gson.Gson;
import com.sendbird.uikit.R;
import com.sendbird.uikit.databinding.SbViewAdminMessageComponentBinding;
import com.sendbird.uikit.model.admin.AdminMessageData;
import com.sendbird.uikit.utils.TextUtils;

public class AdminMessageView extends BaseMessageView {
    private SbViewAdminMessageComponentBinding binding;

    @Override
    public SbViewAdminMessageComponentBinding getBinding() {
        return binding;
    }

    @Override
    public View getLayout() {
        return binding.getRoot();
    }

    public AdminMessageView(Context context) {
        this(context, null);
    }

    public AdminMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.sb_message_admin_style);
    }

    public AdminMessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MessageView_Admin, defStyle, 0);
        try {
            this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.sb_view_admin_message_component, this, true);
            int textAppearance = a.getResourceId(R.styleable.MessageView_Admin_sb_admin_message_text_appearance, R.style.SendbirdCaption2OnLight02);
            int backgroundResourceId = a.getResourceId(R.styleable.MessageView_Admin_sb_admin_message_background, android.R.color.transparent);

            binding.tvMessage.setTextAppearance(context, textAppearance);
            binding.tvMessage.setBackgroundResource(backgroundResourceId);
        } finally {
            a.recycle();
        }
    }

    public void drawAdminMessage(BaseMessage message, BaseChannel channel) {
        try {
            String data = message.getData();
            AdminMessageData adminMessageData = new Gson().fromJson(data, AdminMessageData.class);
            String adminMessageContent = adminMessageData.getContent(channel);

            binding.tvMessage.setText(TextUtils.isEmpty(adminMessageContent) ? message.getMessage() : adminMessageContent);
        } catch (Exception e) {
            binding.tvMessage.setText(message.getMessage());
        }
    }

    @BindingAdapter(value = {"message", "channel"}, requireAll = true)
    public static void drawMessage(AdminMessageView view, AdminMessage message, BaseChannel channel) {
        view.drawAdminMessage(message, channel);
    }
}
