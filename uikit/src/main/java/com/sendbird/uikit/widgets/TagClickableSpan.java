package com.sendbird.uikit.widgets;

import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.sendbird.android.Member;

interface OnTagClicked {
    void onTagClicked(String tagName, Member member);
}

public class TagClickableSpan extends ClickableSpan {
    private String name;
    private Member member;
    private OnTagClicked onTagClicked;

    public TagClickableSpan(String name, Member member, OnTagClicked onTagClicked) {
        this.name = name;
        this.member = member;
        this.onTagClicked = onTagClicked;
    }

    @Override
    public void onClick(@NonNull View widget) {
        if (onTagClicked != null) {
            onTagClicked.onTagClicked(name, member);
        }
    }
}