package com.sendbird.uikit.activities.adapter;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sendbird.android.Member;
import com.sendbird.uikit.activities.viewholder.BaseViewHolder;
import com.sendbird.uikit.databinding.SbViewMemberPreviewBinding;
import com.sendbird.uikit.interfaces.OnItemClickListener;
import com.sendbird.uikit.widgets.MemberPreview;

import java.util.List;

public class TagAdapter extends BaseAdapter<Member, BaseViewHolder<Member>> {

    private List<Member> members;
    private OnItemClickListener<Member> listener;

    @Override
    public Member getItem(int position) {
        return members.get(position);
    }

    @Override
    public List<Member> getItems() {
        return members;
    }

    @NonNull
    @Override
    public BaseViewHolder<Member> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemberTagPreviewHolder(SbViewMemberPreviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<Member> holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void setItems(List<Member> memberList) {
        this.members = memberList;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener<Member> listener) {
        this.listener = listener;
    }

    private class MemberTagPreviewHolder extends BaseViewHolder<Member> {
        private final SbViewMemberPreviewBinding binding;

        MemberTagPreviewHolder(@NonNull SbViewMemberPreviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.memberViewHolder.setOnClickListener(v -> {
                int userPosition = getAdapterPosition();
                if (userPosition != NO_POSITION && listener != null) {
                    Member member = getItem(userPosition);
                    listener.onItemClick(v, userPosition, member);
                }
            });

            binding.memberViewHolder.setOnActionMenuClickListener(v -> {
                int userPosition = getAdapterPosition();
                if (userPosition != NO_POSITION && listener != null) {
                    Member member = getItem(userPosition);
                    listener.onItemClick(v, userPosition, member);
                }
            });
        }

        @Override
        public void bind(Member member) {
            binding.memberViewHolder.useActionMenu(false);
            MemberPreview.drawMember(binding.memberViewHolder, member);
            binding.executePendingBindings();
        }
    }
}
