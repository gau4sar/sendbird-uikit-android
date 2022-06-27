package com.sendbird.uikit.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sendbird.android.Member;
import com.sendbird.uikit.activities.adapter.TagAdapter;
import com.sendbird.uikit.interfaces.OnItemClickListener;
import com.sendbird.uikit.interfaces.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class TagView extends ThemeableRecyclerView implements OnItemClickListener<Member> {

    public interface OnUserMentionSelectedListener {
        void onUserMentionSelected(Member member);
    }

    public TagAdapter tagAdapter = new TagAdapter();
    private List<Member> members = new ArrayList<>();
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
        Log.e("nt.dung", "Filter: " + constraint);
        memberFilter.filter(constraint);
    }

    public void setUserList(List<Member> memberList) {
        this.members = memberList;
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

    private class MemberFilter extends Filter {

        class ContactResult {
            List<Member> contacts = new ArrayList<>();

            public ContactResult() {}

            public ContactResult(List<Member> contacts) {
                this.contacts = contacts;
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (TextUtils.isEmpty(constraint)) {
                filterResults.values = new MemberFilter.ContactResult(members);
            } else {
                MemberFilter.ContactResult filterData = new MemberFilter.ContactResult();

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
                if (results.values instanceof MemberFilter.ContactResult) {
                    MemberFilter.ContactResult contactResult = (MemberFilter.ContactResult) results.values;
                    tagAdapter.setItems(contactResult.contacts);
                    tagAdapter.notifyDataSetChanged();
                }
            }
        }

        private boolean match(Member member, CharSequence constraint) {
            String key = constraint.toString().toLowerCase().trim();
            String contactName = member.getNickname();
            return contactName != null && contactName.trim().toLowerCase().startsWith(key);
        }
    }
}
