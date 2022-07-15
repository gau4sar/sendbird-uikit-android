package com.sendbird.uikit.widgets;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordPermissionHandler;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.Member;
import com.sendbird.uikit.R;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.consts.KeyboardDisplayType;
import com.sendbird.uikit.consts.StringSet;
import com.sendbird.uikit.databinding.SbViewMessageInputBinding;
import com.sendbird.uikit.fragments.SendBirdDialogFragment;
import com.sendbird.uikit.interfaces.OnInputModeChangedListener;
import com.sendbird.uikit.interfaces.OnInputTextChangedListener;
import com.sendbird.uikit.interfaces.UserInfo;
import com.sendbird.uikit.log.Logger;
import com.sendbird.uikit.utils.SoftInputUtils;
import com.sendbird.uikit.utils.TextUtils;
import com.sendbird.uikit.utils.ViewUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.text.Regex;

import kotlin.text.Regex;

public class MessageInputView extends FrameLayout {
    private SbViewMessageInputBinding binding;

    private FragmentManager fragmentManager;
    private KeyboardDisplayType displayType = KeyboardDisplayType.Plane;
    private OnClickListener sendClickListener;
    private OnClickListener addClickListener;
    private OnClickListener editCancelClickListener;
    private OnClickListener editSaveClickListener;
    private OnClickListener replyCloseButtonClickListener;
    private OnInputTextChangedListener inputTextChangedListener;
    private OnInputTextChangedListener editModeTextChangedListener;
    private OnInputModeChangedListener inputModeChangedListener;
    private Mode mode;
    private int addButtonVisibility = VISIBLE;
    private boolean showSendButtonAlways;
    private boolean isTagging = false;

    public enum Mode {
        DEFAULT, EDIT, QUOTE_REPLY, AUDIO_RECORD
    }

    public MessageInputView(@NonNull Context context) {
        this(context, null);
    }

    public MessageInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.sb_message_input_style);
    }

    public MessageInputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MessageInput, defStyleAttr, 0);
        try {
            this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.sb_view_message_input, this, true);
            int backgroundId = a.getResourceId(R.styleable.MessageInput_sb_message_input_background, R.color.background_50);
            int textBackgroundId = a.getResourceId(R.styleable.MessageInput_sb_message_input_text_background, R.drawable.sb_message_input_text_background_light);
            int textAppearance = a.getResourceId(R.styleable.MessageInput_sb_message_input_text_appearance, R.style.SendbirdBody3OnLight01);
            ColorStateList hintColor = a.getColorStateList(R.styleable.MessageInput_sb_message_input_text_hint_color);
            int textCursorDrawable = a.getResourceId(R.styleable.MessageInput_sb_message_input_text_cursor_drawable, R.drawable.sb_message_input_cursor_light);
            boolean isEnabled = a.getBoolean(R.styleable.MessageInput_sb_message_input_enable, true);

            int leftButtonTint = a.getResourceId(R.styleable.MessageInput_sb_message_input_left_button_tint, R.color.sb_selector_input_add_color_light);
            int leftButtonBackground = a.getResourceId(R.styleable.MessageInput_sb_message_input_left_button_background, R.drawable.sb_button_uncontained_background_light);
            int rightButtonTint = a.getResourceId(R.styleable.MessageInput_sb_message_input_right_button_tint, R.color.primary_300);
            int rightButtonBackground = a.getResourceId(R.styleable.MessageInput_sb_message_input_right_button_background, R.drawable.sb_button_uncontained_background_light);
            int editSaveButtonTextAppearance = a.getResourceId(R.styleable.MessageInput_sb_message_input_edit_save_button_text_appearance, R.style.SendbirdButtonOnDark01);
            int editSaveButtonTextColor = a.getResourceId(R.styleable.MessageInput_sb_message_input_edit_save_button_text_color, R.color.sb_button_contained_text_color_light);
            int editSaveButtonBackground = a.getResourceId(R.styleable.MessageInput_sb_message_input_edit_save_button_background, R.drawable.sb_button_contained_background_light);
            int editCancelButtonTextAppearance = a.getResourceId(R.styleable.MessageInput_sb_message_input_edit_cancel_button_text_appearance, R.style.SendbirdButtonPrimary300);
            int editCancelButtonTextColor = a.getResourceId(R.styleable.MessageInput_sb_message_input_edit_cancel_button_text_color, R.color.sb_button_uncontained_text_color_light);
            int editCancelButtonBackground = a.getResourceId(R.styleable.MessageInput_sb_message_input_edit_cancel_button_background, R.drawable.sb_button_uncontained_background_light);

            int replyTitleAppearance = a.getResourceId(R.styleable.MessageInput_sb_message_input_quote_reply_title_text_appearance, R.style.SendbirdCaption1OnLight01);
            int replyMessageAppearance = a.getResourceId(R.styleable.MessageInput_sb_message_input_quoted_message_text_appearance, R.style.SendbirdCaption2OnLight03);
            int replyRightButtonIcon = a.getResourceId(R.styleable.MessageInput_sb_message_input_quote_reply_right_icon, R.drawable.icon_close);
            int replyRightButtonTint = a.getResourceId(R.styleable.MessageInput_sb_message_input_quote_reply_right_icon_tint, R.color.onlight_02);
            int replyRightButtonBackground = a.getResourceId(R.styleable.MessageInput_sb_message_input_quote_reply_right_icon_background, R.drawable.sb_button_uncontained_background_light);

            binding.messageInputParent.setBackgroundResource(backgroundId);
            binding.etInputText.setBackgroundResource(textBackgroundId);
            binding.etInputText.setTextAppearance(context, textAppearance);
            if (hintColor != null) {
                binding.etInputText.setHintTextColor(hintColor);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                binding.etInputText.setTextCursorDrawable(textCursorDrawable);
            } else {
                Field f = TextView.class.getDeclaredField(StringSet.mCursorDrawableRes);
                f.setAccessible(true);
                f.set(binding.etInputText, textCursorDrawable);
            }

            setEnabled(isEnabled);

            binding.ibtnAdd.setBackgroundResource(leftButtonBackground);
            ImageViewCompat.setImageTintList(binding.ibtnAdd, AppCompatResources.getColorStateList(context, leftButtonTint));
            binding.ibtnSend.setBackgroundResource(rightButtonBackground);
            ImageViewCompat.setImageTintList(binding.ibtnSend, AppCompatResources.getColorStateList(context, rightButtonTint));
            binding.btnSave.setTextAppearance(context, editSaveButtonTextAppearance);
            binding.btnSave.setTextColor(AppCompatResources.getColorStateList(context, editSaveButtonTextColor));
            binding.btnSave.setBackgroundResource(editSaveButtonBackground);
            binding.btnCancel.setTextAppearance(context, editCancelButtonTextAppearance);
            binding.btnCancel.setTextColor(AppCompatResources.getColorStateList(context, editCancelButtonTextColor));
            binding.btnCancel.setBackgroundResource(editCancelButtonBackground);

            binding.ivQuoteReplyMessageImage.setRadius(getResources().getDimensionPixelSize(R.dimen.sb_size_8));
            binding.tvQuoteReplyTitle.setTextAppearance(context, replyTitleAppearance);
            binding.tvQuoteReplyMessage.setTextAppearance(context, replyMessageAppearance);
            binding.ivQuoteReplyClose.setImageResource(replyRightButtonIcon);
            ImageViewCompat.setImageTintList(binding.ivQuoteReplyClose, AppCompatResources.getColorStateList(context, replyRightButtonTint));
            binding.ivQuoteReplyClose.setBackgroundResource(replyRightButtonBackground);
            final int dividerColor = SendBirdUIKit.isDarkMode() ? R.color.ondark_04 : R.color.onlight_04;
            binding.ivReplyDivider.setBackgroundColor(getResources().getColor(dividerColor));
            binding.etInputText.setOnClickListener(v -> {
                showKeyboard();
            });

            binding.etInputText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (!TextUtils.isEmpty(s) && Mode.EDIT != getInputMode() || showSendButtonAlways) {
                        setSendButtonVisibility(View.VISIBLE);
                        setAudioButtonVisibility(View.GONE);
                    } else {
                        setSendButtonVisibility(View.GONE);
                        setAudioButtonVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (editModeTextChangedListener != null && Mode.EDIT == getInputMode()) {
                        editModeTextChangedListener.onInputTextChanged(s, start, before, count);
                    }
                    if (inputTextChangedListener != null && Mode.EDIT != getInputMode()) {
                        inputTextChangedListener.onInputTextChanged(s, start, before, count);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!TextUtils.isEmpty(s) && Mode.EDIT != getInputMode() || showSendButtonAlways) {
                        setSendButtonVisibility(View.VISIBLE);
                        setAudioButtonVisibility(View.GONE);
                    } else {
                        setSendButtonVisibility(View.GONE);
                        setAudioButtonVisibility(View.VISIBLE);
                    }

                    checkForTag(s.toString());
                }
            });
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            a.recycle();
        }
    }

    public void initAudioRecordView(Activity activity, OnRecordListener recordListener) {
        binding.recordButton.setRecordView(binding.recordView);
        binding.recordView.setOnRecordListener(recordListener);
        binding.recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
                if (recordPermissionAvailable) {
                    return true;
                }


                ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.RECORD_AUDIO}, 0);
                return false;
            }
        });
    }

    public void initTagView(List<Member> memberList, TagView.OnUserMentionSelectedListener onUserMentionSelectedListener) {
        binding.tagView.setUserList(memberList);
        binding.tagView.setOnUserMentionSelectedListener(member -> {
            enableTagView(false);
            insertTag(member);
            onUserMentionSelectedListener.onUserMentionSelected(member);
        });
    }

    public void updateTagView(List<Member> memberList) {
        binding.tagView.updateUserList(memberList);
    }

    public void setInputMode(@NonNull final Mode mode) {
        final Mode before = this.mode;
        this.mode = mode;
        if (Mode.EDIT == mode) {
            setQuoteReplyPanelVisibility(GONE);
            setEditPanelVisibility(VISIBLE);
            setAudioPanelVisibility(GONE);
            binding.ibtnAdd.setVisibility(GONE);
        } else if (Mode.QUOTE_REPLY == mode) {
            setQuoteReplyPanelVisibility(VISIBLE);
            setEditPanelVisibility(GONE);
            setAudioPanelVisibility(GONE);
            setAddButtonVisibility(addButtonVisibility);
        } else if (Mode.AUDIO_RECORD == mode) {
            setQuoteReplyPanelVisibility(GONE);
            setEditPanelVisibility(GONE);
            setAddButtonVisibility(GONE);
            setAudioPanelVisibility(VISIBLE);
            setInputTextVisibility(GONE);
            setSendButtonVisibility(GONE);
        } else {
            setQuoteReplyPanelVisibility(GONE);
            setEditPanelVisibility(GONE);
            setAudioPanelVisibility(GONE);
            setInputTextVisibility(VISIBLE);
            setAddButtonVisibility(VISIBLE);
        }

        if (inputModeChangedListener != null) {
            inputModeChangedListener.onInputModeChanged(before, mode);
        }
    }

    public void showKeyboard() {
        if (displayType == KeyboardDisplayType.Dialog) {
            showInputDialog();
        } else {
            SoftInputUtils.showSoftKeyboard(binding.etInputText);
        }
    }

    public void drawMessageToReply(@NonNull BaseMessage message) {
        String displayMessage = message.getMessage();
        if (message instanceof FileMessage) {
            final FileMessage fileMessage = (FileMessage) message;
            ViewUtils.drawFileMessageIconToReply(binding.ivQuoteReplyMessageIcon, fileMessage);
            ViewUtils.drawThumbnail(binding.ivQuoteReplyMessageImage, fileMessage);
            binding.ivQuoteReplyMessageIcon.setVisibility(VISIBLE);
            binding.ivQuoteReplyMessageImage.setVisibility(VISIBLE);

            if (fileMessage.getType().contains(StringSet.gif)) {
                displayMessage = StringSet.gif.toUpperCase();
            } else if (fileMessage.getType().startsWith(StringSet.image)) {
                displayMessage = TextUtils.capitalize(StringSet.photo);
            } else if (fileMessage.getType().startsWith(StringSet.video)) {
                displayMessage = TextUtils.capitalize(StringSet.video);
            } else if (fileMessage.getType().startsWith(StringSet.audio)) {
                displayMessage = TextUtils.capitalize(StringSet.audio);
            } else {
                displayMessage = fileMessage.getName();
            }
        } else {
            binding.ivQuoteReplyMessageIcon.setVisibility(GONE);
            binding.ivQuoteReplyMessageImage.setVisibility(GONE);
        }
        if (null != message.getSender()) {
            binding.tvQuoteReplyTitle.setText(
                    String.format(getContext().getString(R.string.sb_text_reply_to),
                            message.getSender().getNickname()));
        }
        binding.tvQuoteReplyMessage.setText(displayMessage);
    }

    public SbViewMessageInputBinding getBinding() {
        return binding;
    }

    public View getLayout() {
        return this;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        binding.ibtnAdd.setEnabled(enabled);
        binding.etInputText.setEnabled(enabled);
        binding.ibtnSend.setEnabled(enabled);
    }

    public void setSendButtonVisibility(int visibility) {
        binding.ibtnSend.setVisibility(visibility);
    }

    public void setAudioButtonVisibility(int visibility) {
        binding.recordButton.setVisibility(visibility);
    }

    public void setOnSendClickListener(OnClickListener sendClickListener) {
        this.sendClickListener = sendClickListener;
        binding.ibtnSend.setOnClickListener(sendClickListener);
    }

    public void setOnAudioLongClickListener() {
        binding.recordButton.setOnLongClickListener(v -> {
            setInputMode(Mode.AUDIO_RECORD);
            return true;
        });
    }

    public void setSendImageResource(@DrawableRes int sendImageResource) {
        binding.ibtnSend.setImageResource(sendImageResource);
    }

    public void setSendImageButtonTint(ColorStateList tint) {
        ImageViewCompat.setImageTintList(binding.ibtnSend, tint);
    }

    public void showSendButtonAlways(boolean always) {
        showSendButtonAlways = always;
    }

    public void setAddButtonVisibility(int visibility) {
        addButtonVisibility = visibility;
        binding.ibtnAdd.setVisibility(visibility);
    }

    public void setOnInputModeChangedListener(@NonNull OnInputModeChangedListener inputModeChangedListener) {
        this.inputModeChangedListener = inputModeChangedListener;
    }

    public void setOnAddClickListener(OnClickListener addClickListener) {
        this.addClickListener = addClickListener;
        binding.ibtnAdd.setOnClickListener(addClickListener);
    }

    public void setAddImageResource(@DrawableRes int addImageResource) {
        binding.ibtnAdd.setImageResource(addImageResource);
    }

    public void setAddImageButtonTint(ColorStateList tint) {
        ImageViewCompat.setImageTintList(binding.ibtnAdd, tint);
    }

    public void setEditPanelVisibility(int visibility) {
        binding.editPanel.setVisibility(visibility);
    }

    public void setAudioPanelVisibility(int visibility) {
        binding.recordView.setVisibility(visibility);
    }

    public void setInputTextVisibility(int visibility) {
        binding.inputLayout.setVisibility(visibility);
    }

    public void setQuoteReplyPanelVisibility(int visibility) {
        binding.quoteReplyPanel.setVisibility(visibility);
        binding.ivReplyDivider.setVisibility(visibility);
    }

    public void setOnEditCancelClickListener(OnClickListener editCancelClickListener) {
        this.editCancelClickListener = editCancelClickListener;
        binding.btnCancel.setOnClickListener(editCancelClickListener);
    }

    public void setOnEditSaveClickListener(OnClickListener editSaveClickListener) {
        this.editSaveClickListener = editSaveClickListener;
        binding.btnSave.setOnClickListener(editSaveClickListener);
    }

    public void setOnReplyCloseClickListener(OnClickListener replyCloseButtonClickListener) {
        this.replyCloseButtonClickListener = replyCloseButtonClickListener;
        binding.ivQuoteReplyClose.setOnClickListener(replyCloseButtonClickListener);
    }

    public void setOnInputTextChangedListener(OnInputTextChangedListener inputTextChangedListener) {
        this.inputTextChangedListener = inputTextChangedListener;
    }

    public void setOnEditModeTextChangedListener(OnInputTextChangedListener inputTextChangedListener) {
        this.editModeTextChangedListener = inputTextChangedListener;
    }

    public void setInputText(String text) {
        binding.etInputText.setText(text);
        if (text != null) {
            binding.etInputText.setSelection(text.length());
        }
    }

    public String getInputText() {
        Editable editable = binding.etInputText.getText();
        return editable != null ? editable.toString().trim() : null;
    }

    public void setInputTextHint(String hint) {
        binding.etInputText.setHint(hint);
    }

    public EditText getInputEditText() {
        return binding.etInputText;
    }

    public void setKeyboardDisplayType(@NonNull FragmentManager fragmentManager, KeyboardDisplayType displayType) {
        this.fragmentManager = fragmentManager;
        this.displayType = displayType;
        if (displayType == KeyboardDisplayType.Dialog) {
            binding.etInputText.setInputType(InputType.TYPE_NULL);
        } else {
            binding.etInputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
    }

    /**
     * @deprecated As of 2.2.0xx, replaced by {@link MessageInputView#getInputMode()}
     */
    @Deprecated
    public boolean isEditMode() {
        return Mode.EDIT == this.mode;
    }

    public Mode getInputMode() {
        return this.mode;
    }

    private void showInputDialog() {
        final SendBirdDialogFragment.Builder builder = new SendBirdDialogFragment.Builder();
        MessageInputView messageInputView = createDialogInputView();
        builder.setContentView(messageInputView)
                .setDialogGravity(SendBirdDialogFragment.DialogGravity.BOTTOM);
        SendBirdDialogFragment dialogFragment = builder.create();

        final Context context = messageInputView.getContext();
        final int prevSoftInputMode = SoftInputUtils.getSoftInputMode(context);
        SoftInputUtils.setSoftInputMode(context, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        if (sendClickListener != null) {
            messageInputView.setOnSendClickListener(v -> {
                dialogFragment.dismiss();
                binding.ibtnSend.postDelayed(() -> {
                    sendClickListener.onClick(binding.ibtnSend);
                    SoftInputUtils.setSoftInputMode(context, prevSoftInputMode);
                }, 200);
            });
        }

        if (addClickListener != null) {
            messageInputView.setOnAddClickListener(v -> {
                dialogFragment.dismiss();
                binding.ibtnAdd.postDelayed(() -> {
                    addClickListener.onClick(binding.ibtnAdd);
                    SoftInputUtils.setSoftInputMode(context, prevSoftInputMode);
                }, 200);
            });
        }

        if (editSaveClickListener != null) {
            messageInputView.setOnEditSaveClickListener(v -> {
                setInputText(messageInputView.getInputText());
                dialogFragment.dismiss();
                binding.btnSave.postDelayed(() -> {
                    editSaveClickListener.onClick(binding.btnSave);
                    SoftInputUtils.setSoftInputMode(context, prevSoftInputMode);
                }, 200);
            });
        }

        if (editCancelClickListener != null) {
            messageInputView.setOnEditCancelClickListener(v -> {
                dialogFragment.dismiss();
                binding.btnCancel.postDelayed(() -> {
                    editCancelClickListener.onClick(binding.btnCancel);
                    SoftInputUtils.setSoftInputMode(context, prevSoftInputMode);
                }, 200);
            });
        }

        if (replyCloseButtonClickListener != null) {
            messageInputView.setOnReplyCloseClickListener(v -> {
                dialogFragment.dismiss();
                binding.ivQuoteReplyClose.postDelayed(() -> {
                    replyCloseButtonClickListener.onClick(binding.ivQuoteReplyClose);
                    SoftInputUtils.setSoftInputMode(context, prevSoftInputMode);
                }, 200);
            });
        }

        messageInputView.setOnInputTextChangedListener((s, start, before, count) -> {
            if (editModeTextChangedListener != null && Mode.EDIT == getInputMode()) {
                editModeTextChangedListener.onInputTextChanged(s, start, before, count);
            }
            if (inputTextChangedListener != null && Mode.EDIT != getInputMode()) {
                inputTextChangedListener.onInputTextChanged(s, start, before, count);
            }
            if (Mode.EDIT != getInputMode()) {
                setInputText(s.toString());
            }
        });

        dialogFragment.showSingle(fragmentManager);
        messageInputView.showKeyboard();

        final Dialog dialog = dialogFragment.getDialog();
        if (dialog != null) {
            dialog.setOnDismissListener(d -> {
                dialogFragment.dismiss();
                setInputMode(Mode.DEFAULT);
                binding.getRoot().postDelayed(() -> {
                    SoftInputUtils.setSoftInputMode(context, prevSoftInputMode);
                }, 200);
            });
        }
    }

    private MessageInputView createDialogInputView() {
        final MessageInputView messageInputView = new MessageInputView(getContext());
        if (showSendButtonAlways) messageInputView.setSendButtonVisibility(VISIBLE);
        messageInputView.showSendButtonAlways(showSendButtonAlways);

        messageInputView.setInputMode(mode);
        if (Mode.EDIT == mode) {
            messageInputView.setInputText(getInputText());
        } else if (Mode.QUOTE_REPLY == mode) {
            messageInputView.getBinding().ivQuoteReplyMessageIcon.setVisibility(binding.ivQuoteReplyMessageIcon.getVisibility());
            messageInputView.getBinding().ivQuoteReplyMessageImage.setVisibility(binding.ivQuoteReplyMessageImage.getVisibility());
            messageInputView.getBinding().ivQuoteReplyMessageIcon.setImageDrawable(binding.ivQuoteReplyMessageIcon.getDrawable());
            messageInputView.getBinding().ivQuoteReplyMessageImage.getContent().setImageDrawable(binding.ivQuoteReplyMessageImage.getContent().getDrawable());
            messageInputView.getBinding().tvQuoteReplyTitle.setText(binding.tvQuoteReplyTitle.getText());
            messageInputView.getBinding().tvQuoteReplyMessage.setText(binding.tvQuoteReplyMessage.getText());
        }

        messageInputView.getBinding().ibtnSend.setImageDrawable(binding.ibtnSend.getDrawable());
        messageInputView.getBinding().ibtnAdd.setImageDrawable(binding.ibtnAdd.getDrawable());
        messageInputView.getBinding().etInputText.setHint(binding.etInputText.getHint());

        return messageInputView;
    }

    public void enableTagView(boolean enable) {
        if (enable) {
            if (!isTagging) {
                isTagging = true;
                binding.tagView.setVisibility(View.VISIBLE);
                binding.tagView.filter("");
            }
        }
        else {
            isTagging = false;
            binding.tagView.setVisibility(GONE);
        }
    }

    private void checkForTag(String text) {
        String tagText = text;
        if (!TextUtils.isEmpty(tagText)) {
            int start = binding.etInputText.getSelectionStart();
            if (start >= 0) {
                tagText = tagText.substring(0, start);
            }
            showTagViewIfNeed(tagText);
        } else {
            enableTagView(false);
        }
    }

    private void showTagViewIfNeed(String text) {
        String regex = "\\w*[@]\\w*$";
        if (!TextUtils.isEmpty(text)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String tag = matcher.group();
                String filter = tag.replaceAll("^@(.*@|)", "");
                if (isTagging) {
                    filterTagView(filter);
                } else {
                    enableTagView(true);
                }
            } else {
                enableTagView(false);
            }
        } else {
            enableTagView(false);
        }
    }

    private void filterTagView(String constraint) {
        if (isTagging) {
            binding.tagView.filter(constraint);
        }
    }

    public void insertTag(Member member) {
        int position;
        String phoneNumber = member.getMetaData("phone");
        String tagName = SendBirdUIKit.findPhoneBookName(phoneNumber);

        Editable editable = binding.etInputText.getEditableText();
        if (editable == null) return;

        int start = binding.etInputText.getSelectionStart();
        for (position = start - 1; position >= 0; position--) {
            if (editable.toString().charAt(position) == '@') {
                break;
            }
        }

        if (position < start - 1) {
            editable.delete(position + 1, start);
        }

        tagName = tagName + " ";
        int newStart = binding.etInputText.getSelectionStart();
        int end = newStart + tagName.length();
        if (newStart >= 0 && end >= 0 && newStart < end) {
            editable.insert(newStart, tagName);
            editable.setSpan(new TagClickableSpan(tagName, member.getUserId(), Color.parseColor("#491389"), null), newStart, end - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
