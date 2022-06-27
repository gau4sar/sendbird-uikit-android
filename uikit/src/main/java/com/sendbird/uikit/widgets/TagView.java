package com.sendbird.uikit.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sendbird.android.Member;
import com.sendbird.uikit.activities.adapter.TagAdapter;
import com.sendbird.uikit.interfaces.OnItemClickListener;
import com.sendbird.uikit.interfaces.UserInfo;

import java.util.List;

public class TagView extends ThemeableRecyclerView implements OnItemClickListener<Member> {

    public interface OnUserMentionSelectedListener {
        void onUserMentionSelected(Member member);
    }

    public TagAdapter tagAdapter = new TagAdapter();
    private OnUserMentionSelectedListener onUserMentionSelectedListener;

    public TagView(@NonNull Context context) {
        super(context);
    }

    public TagView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TagView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnUserMentionSelectedListener(OnUserMentionSelectedListener onUserMentionSelectedListener) {
        this.onUserMentionSelectedListener = onUserMentionSelectedListener;
    }

    public void setUserList(List<Member> memberList) {
        tagAdapter.setItems(memberList);
        tagAdapter.setItemClickListener(this);
        setAdapter(tagAdapter);
    }

    @Override
    public void onItemClick(View view, int position, Member data) {
        if (onUserMentionSelectedListener != null) {
            onUserMentionSelectedListener.onUserMentionSelected(data);
        }
    }
}
