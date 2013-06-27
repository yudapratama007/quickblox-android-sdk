package com.quickblox.chat_v2.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickblox.chat_v2.R;
import com.quickblox.chat_v2.core.ChatApplication;
import com.quickblox.chat_v2.ui.activities.UserProfileActivity;
import com.quickblox.chat_v2.utils.GlobalConsts;
import com.quickblox.module.chat.model.QBChatRoster;
import com.quickblox.module.users.model.QBUser;

import org.jivesoftware.smack.XMPPException;

public class TopBar extends RelativeLayout {

    public static final String CHAT_ACTIVITY = "Dialog";
    public static final String NEW_DIALOG_ACTIVITY = "New Dialog";
    public static final String PROFILE_ACTIVITY = "Profile";
    public static final String ROOM_ACTIVITY = "Group chat";

    private TextView screenTitle;
    private ImageView userAvatar;
    private ProgressBar progBar;
    private QBChatRoster mQBChatRoster;


    public enum ContactAction {ADD, REMOVE}

    ;

    private ContactAction action = ContactAction.ADD;

    private static String[] dialogTabledata = new String[2];
    private QBUser friend;

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.top_bar, this);
        setBackgroundResource(android.R.color.darker_gray);
        initViews();
        dialogTabledata[0] = getContext().getResources().getString(R.string.chat_dialog_view_profile);
        mQBChatRoster = ChatApplication.getInstance().getQbRoster();
    }

    private void initViews() {

        screenTitle = (TextView) findViewById(R.id.screen_title);
        userAvatar = (ImageView) findViewById(R.id.user_avatar_iv);
        progBar = (ProgressBar) findViewById(R.id.attach_progress_indicator);
        userAvatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(dialogTabledata);
            }

        });

    }

    private void showDialog(String[] data) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        adb.setTitle(R.string.chat_dialog_name);

        adb.setItems(data, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        Intent intent = new Intent(getContext(), UserProfileActivity.class);
                        intent.putExtra(GlobalConsts.FRIEND_ID, String.valueOf(friend.getId()));
                        getContext().startActivity(intent);
                        break;

                    case 1:

                        if (action == ContactAction.ADD) {

                            if (friend != null) {
                                setFriendParams(friend, true);
                                try {
                                    mQBChatRoster.createEntry(friend.getId(), friend.getLogin(), null);
                                } catch (XMPPException e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            mQBChatRoster.removeUserFromRoster(friend.getId());
                            if (friend != null) {
                                setFriendParams(friend, false);
                            }
                        }

                        break;
                }
            }
        });

        adb.create().show();
    }

    public void setFragmentParams(String fragmentName, int isUserPicVisible, boolean isAvatarClicable) {
        if (fragmentName != null) {
            screenTitle.setText(fragmentName);
        }
        userAvatar.setVisibility(isUserPicVisible);
        userAvatar.setClickable(isAvatarClicable);
    }

    public void swichProgressBarVisibility(int isVisbleConstant) {
        progBar.setVisibility(isVisbleConstant);
    }

    public void setFriendParams(QBUser friend, boolean isContacts) {
        this.friend = friend;

        if (isContacts) {
            action = ContactAction.REMOVE;
            dialogTabledata[1] = getContext().getResources().getString(R.string.chat_dialog_remove_contact);
        } else {
            action = ContactAction.ADD;
            dialogTabledata[1] = getContext().getResources().getString(R.string.chat_dialog_add_contact);
        }
    }


}