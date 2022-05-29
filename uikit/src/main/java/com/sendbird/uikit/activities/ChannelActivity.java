package com.sendbird.uikit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;

import com.sendbird.android.Member;
import com.sendbird.uikit.R;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.consts.StringSet;
import com.sendbird.uikit.fragments.ChannelFragment;
import com.sendbird.uikit.model.HighlightMessageInfo;
import com.sendbird.uikit.utils.ContextUtils;
import com.sendbird.uikit.utils.TextUtils;
import com.sendbird.uikit.widgets.AudioManager;

import java.util.List;

/**
 * Activity displays a list of messages from a channel.
 */
public class ChannelActivity extends AppCompatActivity {

    /**
     * Create an intent for a {@link ChannelActivity}.
     *
     * @param context A Context of the application package implementing this class.
     * @param channelUrl the url of the channel will be implemented.
     * @return ChannelActivity Intent
     */
    public static Intent newIntent(@NonNull Context context, @NonNull String channelUrl) {
        return newIntentFromCustomActivity(context, ChannelActivity.class, channelUrl, null);
    }

    /**
     * Create an intent for a {@link ChannelActivity}.
     *
     * @param context A Context of the application package implementing this class.
     * @param channelUrl the url of the channel will be implemented.
     * @return ChannelActivity Intent
     */
    public static Intent newIntent(@NonNull Context context, @NonNull String channelUrl, @Nullable String title) {
        return newIntentFromCustomActivity(context, ChannelActivity.class, channelUrl, title);
    }

    /**
     * Create an intent for a custom activity. The custom activity must inherit {@link ChannelActivity}.
     *
     * @param context A Context of the application package implementing this class.
     * @param cls The activity class that is to be used for the intent.
     * @param channelUrl the url of the channel will be implemented.
     * @return Returns a newly created Intent that can be used to launch the activity.
     * @since 1.1.2
     */
    public static Intent newIntentFromCustomActivity(@NonNull Context context, @NonNull Class<? extends ChannelActivity> cls, @NonNull String channelUrl, @Nullable String title) {
        return new IntentBuilder(context, cls, channelUrl, title).build();
    }

    private String url;
    private View.OnClickListener onBackClickListener;
    private View.OnClickListener onInfoClickListener;
    private ChannelFragment channelFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SendBirdUIKit.isDarkMode() ? R.style.SendBird_Dark : R.style.SendBird);
        setContentView(getLayoutId());

        initialize();

        url = getIntent().getStringExtra(StringSet.KEY_CHANNEL_URL);
        String title = getIntent().getStringExtra(StringSet.KEY_HEADER_TITLE);
        if (TextUtils.isEmpty(url)) {
            ContextUtils.toastError(this, R.string.sb_text_error_get_channel);
        } else {
            channelFragment = createChannelFragment(url, title);
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();
            manager.beginTransaction()
                    .replace(R.id.sb_fragment_container, channelFragment)
                    .commit();
        }

        AudioManager.getInstance().attachLifecycle(getLifecycle());
    }


    public int getLayoutId() {
        return R.layout.sb_activity;
    }

    public void initialize() {

    }

    public String getChannelUrl() {
        return url;
    }

    public List<Member> getMembers() {
        return channelFragment.getMembers();
    }

    public boolean isSingleChat() {
        return channelFragment.isSingleChat();
    }

    public void setOnBackClickListener(View.OnClickListener backClickListener) {
        this.onBackClickListener = backClickListener;
    }

    public void setOnInfoClickListener(View.OnClickListener infoClickListener) {
        this.onInfoClickListener = infoClickListener;
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(StringSet.KEY_ACTION_CLEAR_CHAT_NOTIFICATION);
        intent.putExtra(StringSet.KEY_CHANNEL_URL, url);
        sendBroadcast(intent);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        AudioManager.getInstance().detachLifecycle(getLifecycle());
        super.onDestroy();
    }

    /**
     * It will be called when the ChannelActiviy is being created.
     *
     * @return a new channel fragment.
     * @since 1.0.4
     */
    protected ChannelFragment createChannelFragment(@NonNull String channelUrl, @Nullable String title) {
        final Intent intent = getIntent();
        ChannelFragment.Builder builder = new ChannelFragment.Builder(channelUrl)
                .setUseHeader(true)
                .setStartingPoint(intent.getLongExtra(StringSet.KEY_STARTING_POINT, Long.MAX_VALUE));
        if (title != null) {
            builder.setHeaderTitle(title);
        }
        if (intent.hasExtra(StringSet.KEY_HIGHLIGHT_MESSAGE_INFO)) {
            builder.setHighlightMessageInfo(intent.getParcelableExtra(StringSet.KEY_HIGHLIGHT_MESSAGE_INFO));
        }
        if (intent.hasExtra(StringSet.KEY_FROM_SEARCH_RESULT)) {
            builder.setUseHeaderRightButton(intent.getBooleanExtra(StringSet.KEY_FROM_SEARCH_RESULT, false));
        }

        builder.setHeaderLeftButtonListener(onBackClickListener);
        builder.setHeaderRightButtonListener(onInfoClickListener);
        return builder.build();
    }

    public static class IntentBuilder {
        private final Context context;
        private final String channelUrl;
        private final String title;
        private long startingPoint = Long.MAX_VALUE;
        private HighlightMessageInfo highlightMessageInfo;
        private Class<? extends ChannelActivity> customClass = ChannelActivity.class;

        /**
         * Create an intent for a {@link ChannelActivity}.
         *
         * @param context A Context of the application package implementing this class.
         * @param channelUrl The url of the channel will be implemented.
         * @since 2.1.0
         */
        public IntentBuilder(@NonNull Context context, @NonNull String channelUrl, @Nullable String title) {
            this.context = context;
            this.channelUrl = channelUrl;
            this.title = title;
        }

        /**
         * Create an intent for a {@link ChannelActivity}.
         *
         * @param context A Context of the application package implementing this class.
         * @param customClass The activity class that is to be used for the intent.
         * @param channelUrl The url of the channel will be implemented.
         * @since 2.1.0
         */
        public IntentBuilder(@NonNull Context context, @NonNull Class<? extends ChannelActivity> customClass, @NonNull String channelUrl, @Nullable String title) {
            this.context = context;
            this.channelUrl = channelUrl;
            this.customClass = customClass;
            this.title = title;
        }

        /**
         * Sets the timestamp to load the messages with.
         *
         * @param startingPoint A timestamp to load initially.
         * @return This Builder object to allow for chaining of calls to set methods.
         * @since 2.1.0
         */
        public IntentBuilder setStartingPoint(long startingPoint) {
            this.startingPoint = startingPoint;
            return this;
        }

        /**
         * Sets the information of the message to highlight.
         *
         * @param highlightMessageInfo An information of the message to highlight.
         * @return This Builder object to allow for chaining of calls to set methods.
         * @since 2.1.0
         */
        public IntentBuilder setHighlightMessageInfo(HighlightMessageInfo highlightMessageInfo) {
            this.highlightMessageInfo = highlightMessageInfo;
            return this;
        }

        /**
         * Creates an {@link Intent} with the arguments supplied to this builder.
         *
         * @return The ChannelActivity {@link Intent} applied to the {@link Bundle}.
         * @since 2.1.0
         */
        public Intent build() {
            Intent intent = new Intent(context, customClass);
            intent.putExtra(StringSet.KEY_CHANNEL_URL, channelUrl);
            intent.putExtra(StringSet.KEY_STARTING_POINT, startingPoint);
            intent.putExtra(StringSet.KEY_HEADER_TITLE, title);
            if (highlightMessageInfo != null) {
                intent.putExtra(StringSet.KEY_HIGHLIGHT_MESSAGE_INFO, highlightMessageInfo);
            }
            return intent;
        }
    }
}
