package com.sendbird.uikit.utils;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.Sender;
import com.sendbird.android.User;
import com.sendbird.uikit.R;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.consts.StringSet;
import com.sendbird.uikit.model.UserConfigInfo;
import com.sendbird.uikit.widgets.ChannelCoverView;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kotlin.jvm.functions.Function2;

public class ChannelUtils {

    public static Member getOtherMember(GroupChannel channel) {
        List<Member> members = channel.getMembers();
        for (Member member: members) {
            if (!SendBirdUIKit.isItMe(member.getUserId())) {
                return member;
            }
        }
        return null;
    }

    public static User getOtherUser(List<User> users) {
        for (User user: users) {
            if (!SendBirdUIKit.isItMe(user.getUserId())) {
                return user;
            }
        }
        return null;
    }

    public static User findUser(List<User> users, String userId) {
        for (User user: users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public static User findUserByNumber(GroupChannel channel, String phoneNumber) {
        List<Member> members = channel.getMembers();
        for (User user: members) {
            String number = user.getMetaData("phone");
            if (phoneNumber.equals(number)) {
                return user;
            }
        }
        return null;
    }

    private static List<Member> getOtherMembers(GroupChannel channel) {
        List<Member> members = channel.getMembers();
        List<Member> otherMembers = new ArrayList<>();
        String userId = SendBirdUIKit.getAdapter().getUserInfo().getUserId();
        for (Member member: members) {
            if (!member.getUserId().equals(userId)) {
                otherMembers.add(member);
            }
        }
        return otherMembers;
    }

    public static String makeTitleText(GroupChannel channel) {
        boolean isSingleChat = channel.getMemberCount() <= 2 && !channel.isSuper();
        if (isSingleChat) {
            Member otherMember = getOtherMember(channel);
            if (otherMember != null) {
                String phoneNumber = otherMember.getMetaData("phone");
                return SendBirdUIKit.findPhoneBookName(phoneNumber);
            } else {
                return "No members";
            }
        } else {
            String channelName = channel.getName();
            List<Member> otherMembers = getOtherMembers(channel);
            if (!TextUtils.isEmpty(channelName)) return channelName;
            else {
                StringBuilder names = new StringBuilder();
                for (Member member: otherMembers) {
                    String phoneNumber = member.getMetaData("phone");
                    String memberName = SendBirdUIKit.findPhoneBookName(phoneNumber);
                    names.append(memberName);
                    names.append(", ");
                }
                return names.toString();
            }
        }
    }

    public static String makeTitleText(@NonNull Context context, GroupChannel channel) {
        String channelName = channel.getName();
        if (!TextUtils.isEmpty(channelName) && !channelName.equals(StringSet.GROUP_CHANNEL)) {
            return channelName;
        }

        List<Member> members = channel.getMembers();

        String result;
        if (members.size() < 2 || SendBird.getCurrentUser() == null) {
            result = context.getString(R.string.sb_text_channel_list_title_no_members);
        } else if (members.size() == 2) {
            StringBuilder names = new StringBuilder();
            for (Member member : members) {
                if (member.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    continue;
                }

                String nickName = member.getNickname();
                names.append(", ")
                        .append(TextUtils.isEmpty(nickName) ?
                                context.getString(R.string.sb_text_channel_list_title_unknown) : nickName);
            }
            result = names.delete(0, 2).toString();
        } else {
            int count = 0;
            StringBuilder names = new StringBuilder();
            for (Member member : members) {
                if (member.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    continue;
                }

                count++;
                String nickName = member.getNickname();
                names.append(", ")
                        .append(TextUtils.isEmpty(nickName) ?
                                context.getString(R.string.sb_text_channel_list_title_unknown) : nickName);

                if(count >= 10) {
                    break;
                }
            }
            result = names.delete(0, 2).toString();
        }

        return result;
    }

    public static String getLastMessage(Context context, GroupChannel channel) {
        // Bind last message text according to the type of message. Specifically, if
        // the last message is a File Message, there must be special formatting.

        final BaseMessage lastMessage = channel.getLastMessage();
        if (lastMessage == null) {
            return "";
        }

        if (lastMessage instanceof FileMessage) {
            Sender sender = lastMessage.getSender();
            return String.format(context.getString(R.string.sb_text_channel_list_last_message_file),
                    sender != null ? sender.getNickname() : context.getString(R.string.sb_text_channel_list_last_message_file_unknown));
        }
        return lastMessage.getMessage();
    }

    public static void loadImage(ChannelCoverView coverView, String url) {
        List<String> urls = new ArrayList<>();
        urls.add(url);
        coverView.loadImages(urls);
    }

    public static void loadChannelCover(ChannelCoverView coverView, BaseChannel channel) {
        if (channel instanceof GroupChannel) {
            GroupChannel groupChannel = (GroupChannel) channel;
            if (groupChannel.isSuper()) {
                String coverUrl = isDefaultChannelCover(channel) ? "" : channel.getCoverUrl();
                coverView.loadImage(coverUrl);
                return;
            }
            if (groupChannel.isBroadcast() && isDefaultChannelCover(channel)) {
                coverView.drawBroadcastChannelCover();
                return;
            }

            if (isDefaultChannelCover(channel)) {
                List<String> urls = makeProfileUrlsFromChannel(groupChannel);
                coverView.loadImages(urls);
            } else {
                coverView.loadImage(channel.getCoverUrl());
            }
        } else {
            coverView.loadImage(channel.getCoverUrl());
        }
    }

    public static List<String> makeProfileUrlsFromChannel(GroupChannel channel) {
        List<String> urls = new ArrayList<>();
        if (!isDefaultChannelCover(channel)) {
            urls.add(channel.getCoverUrl());
        } else {
            String myUserId = "";
            if (SendBird.getCurrentUser() != null) {
                myUserId = SendBird.getCurrentUser().getUserId();
            }
            List<Member> memberList = channel.getMembers();
            int index = 0;
            while (index < memberList.size() && urls.size() < 4) {
                Member member = memberList.get(index);
                ++index;
                if (member.getUserId().equals(myUserId)) {
                    continue;
                }

                UserConfigInfo userConfigInfo = SendBirdUIKit.getUserConfig(member);
                if (userConfigInfo == null || !userConfigInfo.isShowProfile()) {
                    urls.add(member.getProfileUrl());
                }
            }
        }
        return urls;
    }

    public static boolean isDefaultChannelCover(BaseChannel channel) {
        return TextUtils.isEmpty(channel.getCoverUrl()) || channel.getCoverUrl().contains(StringSet.DEFAULT_CHANNEL_COVER_URL);
    }

    public static String makeTypingText(Context context, List<? extends User> typingUsers) {
        if (typingUsers.size() == 1) {
            return String.format(context.getString(R.string.sb_text_channel_typing_indicator_single),
                    SendBirdUIKit.getPhonebookName(typingUsers.get(0)));
        } else if (typingUsers.size() == 2) {
            return String.format(context.getString(R.string.sb_text_channel_typing_indicator_double),
                    SendBirdUIKit.getPhonebookName(typingUsers.get(0)), SendBirdUIKit.getPhonebookName(typingUsers.get(1)));
        } else {
            return context.getString(R.string.sb_text_channel_typing_indicator_multiple);
        }
    }

    public static void makeLastSeenText(Context context, GroupChannel channel, Function2<Boolean, String, Void> callback) {
        channel.refresh(e -> {
            if (e != null) {
                e.printStackTrace();
                return;
            }

            User other = getOtherMember(channel);
            if (other != null) {
                makeLastSeenText(context, other, callback);
            }
        });
    }

    public static void makeLastSeenText(Context context, User user, Function2<Boolean, String, Void> callback) {
        String lastSeenText = "";
        boolean isOnline = user.getConnectionStatus() == User.ConnectionStatus.ONLINE;
        if (isOnline) {
            lastSeenText = context.getString(R.string.online);
            callback.invoke(true, lastSeenText);
        } else {
            UserConfigInfo userConfigInfo = SendBirdUIKit.getUserConfig(user);
            if (userConfigInfo == null || userConfigInfo.isShowLastSeen()) {
                long lastSeenAt = user.getLastSeenAt();
                LocalDateTime now = LocalDateTime.now();
                LocalDate nowDate = now.toLocalDate();

                LocalDateTime lastSeen = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastSeenAt), ZoneId.systemDefault());
                LocalDate lastSeenDate = lastSeen.toLocalDate();

                Duration duration = Duration.between(lastSeen, now);
                long offsetDays = duration.toDays();

                boolean is24hFormat = DateFormat.is24HourFormat(context);
                String timeFormat = is24hFormat ? "HH:mm" : "hh:mm a";
                Locale locale = SendBirdUIKit.getAdapter().getUserInfo().getLocale();
                if (lastSeenAt < 0) {
                    lastSeenText = context.getString(R.string.last_seen_months_ago);
                } else if (nowDate.isEqual(lastSeenDate)) {
                    lastSeenText = context.getString(R.string.last_seen_at, lastSeen.format(DateTimeFormatter.ofPattern(timeFormat, locale)));
                } else if (offsetDays == 1) {
                    lastSeenText = context.getString(R.string.last_seen_yesterday) + lastSeen.format(DateTimeFormatter.ofPattern(timeFormat, locale));
                } else if (offsetDays > 1 && offsetDays < 7) {
                    lastSeenText = context.getString(R.string.last_seen, lastSeen.format(DateTimeFormatter.ofPattern("EEEE, " + timeFormat, locale)));
                } else if (offsetDays >= 7 && offsetDays < 14) {
                    lastSeenText = context.getString(R.string.last_seen_a_week_ago);
                } else if (offsetDays >= 14 && offsetDays < 28) {
                    lastSeenText = context.getString(R.string.last_seen_few_weeks_ago);
                } else {
                    lastSeenText = context.getString(R.string.last_seen, lastSeen.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", locale)));
                }

                callback.invoke(false, lastSeenText);
            }
        }
    }

    public static boolean isChannelPushOff(GroupChannel channel) {
        return channel.getMyPushTriggerOption() == GroupChannel.PushTriggerOption.OFF;
    }

    public static CharSequence makeMemberCountText(int memberCount) {
        String text = String.valueOf(memberCount);
        if (memberCount > 1000) {
            text = String.format(Locale.US, "%.1fK", memberCount / 1000F);
        }
        return text;
    }
}
