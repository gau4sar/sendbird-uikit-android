package com.sendbird.uikit.interfaces;

import java.util.List;

/**
 * Interface definition for a information of user.
 */
public interface UserInfo {
    /**
     * Provides the identifier of the user.
     *
     * @return the identifier of the user.
     */
    String getUserId();

    /**
     * Provides the nickname of the user.
     *
     * @return the nickname of the user.
     */
    String getNickname();

    /**
     * Provides the profile url of the user.
     *
     * @return the profile url of the user.
     */
    String getProfileUrl();

    String getUserPhoneNumber();

    String getUsername();

    List<String> getTranslationTargetLanguages();

    String getPreferTranslateLanguage();
}
