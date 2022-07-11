package com.sendbird.uikit_messaging_android;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.multidex.MultiDexApplication;

import com.sendbird.android.LogLevel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.handlers.InitResultHandler;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.adapter.SendBirdUIKitAdapter;
import com.sendbird.uikit.interfaces.UserInfo;
import com.sendbird.uikit.widgets.AudioPlayer;
import com.sendbird.uikit_messaging_android.consts.InitState;
import com.sendbird.uikit_messaging_android.fcm.MyFirebaseMessagingService;
import com.sendbird.uikit_messaging_android.utils.PreferenceUtils;
import com.sendbird.uikit_messaging_android.utils.PushUtils;

public class BaseApplication extends MultiDexApplication {

    private static final String APP_ID = "3C52676B-2D0B-4DB0-A0DD-C3879D665F35";
    private static final MutableLiveData<InitState> initState = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceUtils.init(getApplicationContext());

        SendBirdUIKit.init(new SendBirdUIKitAdapter() {
            @Override
            public String getAppId() {
                return APP_ID;
            }

            @Override
            public String getAccessToken() {
                return "";
            }

            @Override
            public UserInfo getUserInfo() {
                return new UserInfo() {
                    @Override
                    public String getUserId() {
                        return "d19b2886-18b6-47e2-93b7-c9388fb10694";
                    }

                    @Override
                    public String getNickname() {
                        return "Nguyen Tien Dzung123";
                    }

                    @Override
                    public String getProfileUrl() {
                        return "https://triva-backend-prod.s3.eu-central-1.amazonaws.com/3208a31f-ac5f-432d-90d0-ab5a305d5325-tmp_image_file4717924989520794310.png";
                    }
                };
            }

            @Override
            public InitResultHandler getInitResultHandler() {
                return new InitResultHandler() {
                    @Override
                    public void onMigrationStarted() {
                        initState.setValue(InitState.MIGRATING);
                    }

                    @Override
                    public void onInitFailed(SendBirdException e) {
                        initState.setValue(InitState.FAILED);
                    }

                    @Override
                    public void onInitSucceed() {
                        initState.setValue(InitState.SUCCEED);
                    }
                };
            }
        }, this);

        boolean useDarkTheme = PreferenceUtils.isUsingDarkTheme();
        SendBirdUIKit.setDefaultThemeMode(useDarkTheme ? SendBirdUIKit.ThemeMode.Dark : SendBirdUIKit.ThemeMode.Light);
        PushUtils.registerPushHandler(new MyFirebaseMessagingService());
        SendBirdUIKit.setLogLevel(SendBirdUIKit.LogLevel.ALL);
        SendBird.setLoggerLevel(LogLevel.VERBOSE);
        SendBirdUIKit.setUseDefaultUserProfile(true);

        AudioPlayer.getInstance().init(this);
    }

    public static LiveData<InitState> initStateChanges() {
        return initState;
    }
}
