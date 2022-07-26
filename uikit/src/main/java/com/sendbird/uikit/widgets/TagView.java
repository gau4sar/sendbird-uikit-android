package com.sendbird.uikit.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.activities.adapter.TagAdapter;
import com.sendbird.uikit.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class TagView extends ThemeableRecyclerView implements OnItemClickListener<Object> {

    public interface OnUserMentionSelectedListener {
        void onUserMentionSelected(Member member);
        void onChannelMentionSelected(GroupChannel channel);
    }

    public TagAdapter tagAdapter = new TagAdapter();
    private final List<Member> members = new ArrayList<>();
    private GroupChannel channel;
    private final MemberFilter memberFilter = new MemberFilter();
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

    public void filter(String constraint) {
        memberFilter.filter(constraint);
    }

    public void setUserList(GroupChannel channel, List<Member> memberList) {
        this.members.clear();
        this.channel = channel;
        for (Member member: memberList) {
            if (!SendBirdUIKit.isItMe(member.getUserId())) {
                this.members.add(member);
            }
        }

        tagAdapter.setItems(memberList, channel);
        tagAdapter.setItemClickListener(this);
        setAdapter(tagAdapter);
    }

    public void updateUserList(List<Member> memberList) {
        this.members.clear();
        for (Member member: memberList) {
            if (!SendBirdUIKit.isItMe(member.getUserId())) {
                this.members.add(member);
            }
        }
        tagAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position, Object data) {
        if (onUserMentionSelectedListener != null) {
            if (data instanceof Member) {
                onUserMentionSelectedListener.onUserMentionSelected((Member) data);
            }
            if (data instanceof GroupChannel) {
                onUserMentionSelectedListener.onChannelMentionSelected((GroupChannel) data);
            }
        }
    }

    private class MemberFilter extends Filter {

        class Result {
            List<Member> contacts = new ArrayList<>();

            public Result() {}

            public Result(List<Member> contacts) {
                this.contacts = contacts;
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (TextUtils.isEmpty(constraint)) {
                filterResults.values = new Result(members);
            } else {
                Result filterData = new Result();

                for (Member contact : members) {
                    if (match(contact, constraint)) {
                        filterData.contacts.add(contact);
                    }
                }

                filterResults.values = filterData;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                if (results.values instanceof Result) {
                    Result result = (Result) results.values;
                    tagAdapter.setItems(result.contacts, channel);
                    tagAdapter.notifyDataSetChanged();
                }
            }
        }

        private boolean match(Member member, CharSequence constraint) {
            String key = constraint.toString().toLowerCase().trim();
            String phoneNumber = member.getMetaData("phone");
            String contactName = SendBirdUIKit.findPhoneBookName(phoneNumber);
            return contactName.trim().toLowerCase().contains(key);
        }
    }
}
