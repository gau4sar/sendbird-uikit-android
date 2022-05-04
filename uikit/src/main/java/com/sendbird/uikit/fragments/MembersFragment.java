package com.sendbird.uikit.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.Member;
import com.sendbird.android.User;
import com.sendbird.uikit.R;
import com.sendbird.uikit.activities.ChannelActivity;
import com.sendbird.uikit.activities.adapter.MemberListAdapter;
import com.sendbird.uikit.consts.StringSet;
import com.sendbird.uikit.databinding.SbFragmentUserTypeListBinding;
import com.sendbird.uikit.model.ReadyStatus;

import java.util.ArrayList;
import java.util.List;

public class MembersFragment extends BaseFragment {

    private SbFragmentUserTypeListBinding binding;
    private MemberListAdapter adapter;

    @Override
    public void onReady(User user, ReadyStatus status) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.sb_fragment_user_type_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.abvMemberList.setVisibility(View.VISIBLE);
        binding.abvMemberList.setLeftImageButtonResource(R.drawable.icon_back);
        binding.abvMemberList.setLeftImageButtonTint(ColorStateList.valueOf(Color.BLACK));
        binding.abvMemberList.setLeftImageButtonClickListener(v -> finish());

        String readStatus = getArguments().getString(StringSet.KEY_READ_STATUS);
        binding.abvMemberList.getTitleTextView().setText(readStatus);
        initMemberList();
    }

    private void initMemberList() {
        String channelUrl = getArguments().getString(StringSet.KEY_CHANNEL_URL);
        ArrayList<String> memberIdList = getArguments().getStringArrayList(StringSet.KEY_MEMBERS);

        GroupChannel.getChannel(channelUrl, (groupChannel, e) -> {
            if (e == null) {
                showMembers(groupChannel, memberIdList);
            }
        });

    }

    private void showMembers(GroupChannel channel, ArrayList<String> memberIdList) {
        if (adapter == null) {
            adapter = new MemberListAdapter();
        }

        List<Member> allMembers = channel.getMembers();
        List<Member> filteredMembers = new ArrayList<>();
        for (Member member: allMembers) {
            if (memberIdList.contains(member.getUserId())) {
                filteredMembers.add(member);
            }
        }
        binding.rvMemberList.setAdapter(adapter);
        binding.rvMemberList.setHasFixedSize(true);

        adapter.setOnItemClickListener((view, position, data) -> openChannel(data));
        adapter.setItems(filteredMembers, Member.Role.NONE);
    }

    private void openChannel(Member member) {
        GroupChannelParams params = new GroupChannelParams();
        params.addUserId(member.getUserId());
        GroupChannel.createDistinctChannelIfNotExist(params, (groupChannel, b, e) ->
                startActivity(ChannelActivity.newIntent(getContext(), groupChannel.getUrl()))
        );
    }
}
