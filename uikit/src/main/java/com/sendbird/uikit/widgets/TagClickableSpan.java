package com.sendbird.uikit.widgets;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.sendbird.android.Member;

public class TagClickableSpan extends ClickableSpan {
    private final String name;
    private final String userId;
    private final OnTagClicked onTagClicked;
    private final int linkColor;

    public TagClickableSpan(String name, String userId, int linkColor, OnTagClicked onTagClicked) {
        this.name = name;
        this.userId = userId;
        this.onTagClicked = onTagClicked;
        this.linkColor = linkColor;
    }

    @Override
    public void onClick(@NonNull View widget) {
        if (onTagClicked != null) {
            onTagClicked.onTagClicked(name, userId);
        }
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.linkColor = linkColor;
        ds.setUnderlineText(true);
    }
}