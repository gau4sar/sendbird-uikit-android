package com.sendbird.uikit.activities.adapter;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.uikit.R;
import com.sendbird.uikit.activities.viewholder.BaseViewHolder;
import com.sendbird.uikit.databinding.SbViewChannelTagBinding;
import com.sendbird.uikit.databinding.SbViewMemberPreviewBinding;
import com.sendbird.uikit.interfaces.OnItemClickListener;
import com.sendbird.uikit.utils.ChannelUtils;
import com.sendbird.uikit.utils.ViewUtils;
import com.sendbird.uikit.widgets.MemberPreview;

import java.util.ArrayList;
import java.util.List;

public class TagAdapter extends BaseAdapter<Object, BaseViewHolder<Object>> {

    private static final int VIEW_TYPE_MEMBER = 0;
    private static final int VIEW_TYPE_CHANNEL = 1;

    private List<Object> data;
    private OnItemClickListener<Object> itemClickListener;

    public TagAdapter() {
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public List<Object> getItems() {
        return data;
    }

    @NonNull
    @Override
    public BaseViewHolder<Object> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_CHANNEL:
                return new ChannelTagPreviewHolder(SbViewChannelTagBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case VIEW_TYPE_MEMBER:
                return new MemberTagPreviewHolder(SbViewMemberPreviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default:
                throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<Object> holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof Member) return VIEW_TYPE_MEMBER;
        else if (item instanceof GroupChannel) return VIEW_TYPE_CHANNEL;
        else return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setItems(List<Member> memberList, GroupChannel channel) {
        this.data = new ArrayList<>();
        this.data.add(channel);
        this.data.addAll(memberList);
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener<Object> listener) {
        this.itemClickListener = listener;
    }

    private class MemberTagPreviewHolder extends BaseViewHolder<Object> {
        private final SbViewMemberPreviewBinding binding;

        MemberTagPreviewHolder(@NonNull SbViewMemberPreviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.memberViewHolder.setOnClickListener(v -> {
                int userPosition = getAdapterPosition();
                if (userPosition != NO_POSITION && itemClickListener != null) {
                    Member member = (Member) getItem(userPosition);
                    itemClickListener.onItemClick(v, userPosition, member);
                }
            });
        }

        @Override
        public void bind(Object member) {
            binding.memberViewHolder.useActionMenu(false);
            MemberPreview.drawMember(binding.memberViewHolder, (Member) member);
            binding.executePendingBindings();
        }
    }

    private class ChannelTagPreviewHolder extends BaseViewHolder<Object> {
        private final SbViewChannelTagBinding binding;

        ChannelTagPreviewHolder(@NonNull SbViewChannelTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int userPosition = getAdapterPosition();
                if (userPosition != NO_POSITION && itemClickListener != null) {
                    GroupChannel channel = (GroupChannel) getItem(userPosition);
                    itemClickListener.onItemClick(v, userPosition, channel);
                }
            });
        }

        @Override
        public void bind(Object obj) {
            GroupChannel channel = (GroupChannel) obj;
            binding.tvNickname.setText(channel.isSuper() ? "Channel" : "Group");

            if (ChannelUtils.isDefaultChannelCover(channel)) {
                binding.ivProfile.setImageResource(R.drawable.ic_default_profile);
            } else {
                ViewUtils.drawProfile(binding.ivProfile, channel.getCoverUrl());
            }
            binding.executePendingBindings();
        }
    }
}
