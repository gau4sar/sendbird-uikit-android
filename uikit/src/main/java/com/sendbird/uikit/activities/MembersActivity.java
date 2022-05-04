package com.sendbird.uikit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.sendbird.android.Member;
import com.sendbird.uikit.R;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.consts.StringSet;
import com.sendbird.uikit.fragments.MemberListFragment;
import com.sendbird.uikit.fragments.MembersFragment;
import com.sendbird.uikit.utils.ContextUtils;
import com.sendbird.uikit.utils.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity displays a list of members joined in a channel.
 */
public class MembersActivity extends AppCompatActivity {

    /**
     * Create an intent for a {@link MembersActivity}.
     *
     * @param context A Context of the application package implementing this class.
     * @return MemberListActivity Intent
     * @since 1.2.0
     */
    public static Intent newIntent(@NonNull Context context, String channelUrl, ArrayList<String> memberIdList, String readStatus) {
        return newIntentFromCustomActivity(context, MembersActivity.class, channelUrl, memberIdList, readStatus);
    }

    /**
     * Create an intent for a custom activity. The custom activity must inherit {@link MembersActivity}.
     *
     * @param context A Context of the application package implementing this class.
     * @param cls     The activity class that is to be used for the intent.
     * @return Returns a newly created Intent that can be used to launch the activity.
     * @since 1.1.2
     */
    public static Intent newIntentFromCustomActivity(@NonNull Context context, @NonNull Class<? extends MembersActivity> cls, String channelUrl, ArrayList<String> memberIdList, String readStatus) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(StringSet.KEY_CHANNEL_URL, channelUrl);
        intent.putStringArrayListExtra(StringSet.KEY_MEMBERS, memberIdList);
        intent.putExtra(StringSet.KEY_READ_STATUS, readStatus);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SendBirdUIKit.isDarkMode() ? R.style.SendBird_Dark : R.style.SendBird);
        setContentView(R.layout.sb_activity);

        String channelUrl = getIntent().getStringExtra(StringSet.KEY_CHANNEL_URL);
        ArrayList<String> membersIdList = getIntent().getStringArrayListExtra(StringSet.KEY_MEMBERS);
        String readStatus = getIntent().getStringExtra(StringSet.KEY_READ_STATUS);

        MembersFragment fragment = new MembersFragment();
        Bundle args = new Bundle();
        args.putString(StringSet.KEY_CHANNEL_URL, channelUrl);
        args.putStringArrayList(StringSet.KEY_MEMBERS, membersIdList);
        args.putString(StringSet.KEY_READ_STATUS, readStatus);
        fragment.setArguments(args);

        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack();
        manager.beginTransaction()
                .replace(R.id.sb_fragment_container, fragment)
                .commit();
    }
}
