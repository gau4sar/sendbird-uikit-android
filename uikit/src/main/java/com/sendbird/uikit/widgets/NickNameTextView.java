package com.sendbird.uikit.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.sendbird.android.Sender;
import com.sendbird.uikit.PhonebookUpdateListener;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.utils.UserUtils;

public class NickNameTextView extends AppCompatTextView implements PhonebookUpdateListener {
    private Sender sender;

    public NickNameTextView(@NonNull Context context) {
        super(context);
    }

    public NickNameTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NickNameTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        SendBirdUIKit.registerPhonebookListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SendBirdUIKit.unregisterPhonebookListener(this);
    }

    public void setSenderName(Sender sender) {
        this.sender = sender;
        String nickname = UserUtils.getDisplayName(getContext(), sender);
        setText(nickname);
    }

    private void refreshSenderName() {
        Handler handler = new Handler(getContext().getMainLooper());
        handler.post(() -> {
            if (sender != null) {
                String nickname = UserUtils.getDisplayName(getContext(), sender);
                setText(nickname);
            }
        });
    }

    @Override
    public void onPhonebookUpdated() {
        refreshSenderName();
    }
}
