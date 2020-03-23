package com.eju.cy.audiovideo.im.helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.GenerateTestUserSig;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.audio.activity.AudioCallMainActivity;
import com.eju.cy.audiovideo.im.entrance.EjuImController;
import com.eju.cy.audiovideo.im.utils.Constants;
import com.eju.cy.audiovideo.im.utils.DemoLog;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudListener;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

import static com.eju.cy.audiovideo.im.helper.CustomMessage.JSON_VERSION_3_ANDROID_IOS_TRTC;
import static com.eju.cy.audiovideo.im.helper.CustomMessage.VIDEO_CALL_ACTION_ACCEPTED;
import static com.eju.cy.audiovideo.im.helper.CustomMessage.VIDEO_CALL_ACTION_DIALING;
import static com.eju.cy.audiovideo.im.helper.CustomMessage.VIDEO_CALL_ACTION_HANGUP;
import static com.eju.cy.audiovideo.im.helper.CustomMessage.VIDEO_CALL_ACTION_LINE_BUSY;
import static com.eju.cy.audiovideo.im.helper.CustomMessage.VIDEO_CALL_ACTION_REJECT;
import static com.eju.cy.audiovideo.im.helper.CustomMessage.VIDEO_CALL_ACTION_SPONSOR_CANCEL;
import static com.eju.cy.audiovideo.im.helper.CustomMessage.VIDEO_CALL_ACTION_SPONSOR_TIMEOUT;


public class CustomAVCallUIController extends TRTCCloudListener {

    private static final String TAG = CustomAVCallUIController.class.getSimpleName();

    private static final int VIDEO_CALL_STATUS_FREE = 1;
    private static final int VIDEO_CALL_STATUS_BUSY = 2;
    private static final int VIDEO_CALL_STATUS_WAITING = 3;
    //视频通话状态
    private int mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;

    private static CustomAVCallUIController mController;

    private long mEnterRoomTime;
    //组装通话所需字段
    private CustomMessage mOnlineCall;
    private ChatLayout mUISender;
    private TRTCDialog mDialog;
    private TRTCCloud mTRTCCloud;
    private static Context context;
    private Context activityContext;
    WeakReference<Activity> mWeakReference;


    private static final int VIDEO_CALL_OUT_GOING_TIME_OUT = 20 * 1000;
    private static final int VIDEO_CALL_OUT_INCOMING_TIME_OUT = 20 * 1000;
    private Handler mHandler = new Handler();
    //对方无应答
    private Runnable mVideoCallOutgoingTimeOut = new Runnable() {
        @Override
        public void run() {
            DemoLog.i(TAG, "time out, dismiss outgoing dialog");
            mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
            sendVideoCallAction(VIDEO_CALL_ACTION_SPONSOR_CANCEL, mOnlineCall);
            dismissDialog();
            ToastUtils.showLong("对方无应答");
        }
    };

    private Runnable mVideoCallIncomingTimeOut = new Runnable() {
        @Override
        public void run() {
            DemoLog.i(TAG, "time out, dismiss incoming dialog");
            mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
            sendVideoCallAction(VIDEO_CALL_ACTION_SPONSOR_TIMEOUT, mOnlineCall);
            dismissDialog();
        }
    };

    private CustomAVCallUIController(Application application) {
        mTRTCCloud = TRTCCloud.sharedInstance(application.getApplicationContext());
        TRTCListener.getInstance().addTRTCCloudListener(this);
        mTRTCCloud.setListener(TRTCListener.getInstance());
    }

    public static CustomAVCallUIController getInstance(Application application) {

        if (mController == null) {
            mController = new CustomAVCallUIController(application);
        }
        return mController;
    }

    public static CustomAVCallUIController getInstance() {

        if (mController == null) {
            mController = new CustomAVCallUIController((Application) context);
        }
        return mController;
    }

    public void setActivityContext(Activity context) {

        this.mWeakReference = new WeakReference<Activity>(context);
        this.activityContext = this.mWeakReference.get();

    }

    public void onCreate(Application application) {
        this.context = application;
        mTRTCCloud = TRTCCloud.sharedInstance(application);
        mTRTCCloud.setListener(this);
    }

    @Override
    public void onError(int errCode, String errMsg, Bundle extraInfo) {
        DemoLog.i(TAG, "trtc onError");
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
        sendVideoCallAction(VIDEO_CALL_ACTION_HANGUP, mOnlineCall);
        Toast.makeText(activityContext, "通话异常: " + errMsg + "[" + errCode + "]", Toast.LENGTH_LONG).show();
        if (mTRTCCloud != null) {
            mTRTCCloud.exitRoom();
        }
    }

    @Override
    public void onEnterRoom(long elapsed) {
        DemoLog.i(TAG, "onEnterRoom " + elapsed);
        Toast.makeText(activityContext, "开始通话", Toast.LENGTH_SHORT).show();
        mEnterRoomTime = System.currentTimeMillis();
    }

    @Override
    public void onExitRoom(int reason) {
        DemoLog.i(TAG, "onExitRoom------ " + reason);
        Toast.makeText(context, "结束通话", Toast.LENGTH_SHORT).show();
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
    }

    public void setUISender(ChatLayout layout) {
        DemoLog.i(TAG, "setUISender: " + layout);
        mUISender = layout;
        if (mCurrentVideoCallStatus == VIDEO_CALL_STATUS_WAITING) {
            boolean success = showIncomingDialingDialog();
            if (success) {
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
            } else {
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                sendVideoCallAction(VIDEO_CALL_ACTION_REJECT, mOnlineCall);
                Toast.makeText(activityContext, "发起通话失败，没有弹出对话框权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onDraw(ICustomMessageViewGroup parent, CustomMessage data) {
        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(activityContext).inflate(R.layout.test_custom_message_av_layout1, null, false);
        parent.addMessageContentView(view);

        if (data == null) {
            DemoLog.i(TAG, "onCalling null data");
            return;
        }
        TextView textView = view.findViewById(R.id.test_custom_message_tv);

        String callingAction = "";
        switch (data.action) {
            // 新接一个电话
            case VIDEO_CALL_ACTION_DIALING:
                callingAction = "[请求通话]";
                break;
            case VIDEO_CALL_ACTION_SPONSOR_CANCEL:
                callingAction = "[取消通话]";
                break;
            case VIDEO_CALL_ACTION_REJECT:
                callingAction = "[拒绝通话]";
                break;
            case VIDEO_CALL_ACTION_SPONSOR_TIMEOUT:
                callingAction = "[无应答]";
                break;
            case VIDEO_CALL_ACTION_ACCEPTED:
                callingAction = "[开始通话]";
                break;
            case VIDEO_CALL_ACTION_HANGUP:
                callingAction = "[结束通话，通话时长：" + DateTimeUtil.formatSeconds(data.duration) + "]";
                break;
            case VIDEO_CALL_ACTION_LINE_BUSY:
                callingAction = "[正在通话中]";
                break;
            default:
                DemoLog.e(TAG, "unknown data.action: " + data.action);
                callingAction = "[不能识别的通话指令]";
                break;
        }
        textView.setText(callingAction);
    }

    //创建视频通话请求
    public void createVideoCallRequest() {
        // 显示通话UI
        boolean success = showOutgoingDialingDialog();
        if (success) {
            //设置当前用户视频状态
            mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
            //组装视频电话
            assembleOnlineCall(null);

            sendVideoCallAction(VIDEO_CALL_ACTION_DIALING, mOnlineCall);
            //删除所有回调消息
            mHandler.removeCallbacksAndMessages(null);
            //对方无应答
            mHandler.postDelayed(mVideoCallOutgoingTimeOut, VIDEO_CALL_OUT_GOING_TIME_OUT);

        } else {
            Toast.makeText(activityContext, "发起通话失败，没有弹出对话框权限", Toast.LENGTH_SHORT).show();
        }
    }


    //创建视频通话请求
    public void createVideoCallRequest(Context context, String listeningUserId, String roomId) {
        this.activityContext = context;
        // 显示通话UI
        boolean success = showOutgoingDialingDialog();
        if (success) {
            //设置当前用户视频状态
            mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
            //组装视频电话
            assembleOnlineCall(null, listeningUserId);

            sendVideoCallAction(VIDEO_CALL_ACTION_DIALING, mOnlineCall);
            //删除所有回调消息
            mHandler.removeCallbacksAndMessages(null);
            //对方无应答
            mHandler.postDelayed(mVideoCallOutgoingTimeOut, VIDEO_CALL_OUT_GOING_TIME_OUT);

        } else {
            Toast.makeText(activityContext, "发起通话失败，没有弹出对话框权限", Toast.LENGTH_SHORT).show();
        }
    }


    //创建视频通话请求
    public void createVideoCallRequest(String userId,
                                       String userPortrait,
                                       String userName,

                                       String othersUserId,
                                       String othersUserPortrait,
                                       String othersUserName,
                                       int roomId,
                                       boolean isAudioCall) {

        // 显示通话UI
        boolean success = showOutgoingDialingDialog();


        if (success) {
            //设置当前用户视频状态
            mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
            //组装视频电话
            assembleOnlineCall(null, userId, userPortrait, userName, othersUserId, othersUserPortrait, othersUserName, roomId, isAudioCall);

            sendVideoCallAction(VIDEO_CALL_ACTION_DIALING, mOnlineCall);
            //删除所有回调消息
            mHandler.removeCallbacksAndMessages(null);
            //对方无应答
            mHandler.postDelayed(mVideoCallOutgoingTimeOut, VIDEO_CALL_OUT_GOING_TIME_OUT);

        } else {
            Toast.makeText(activityContext, "发起通话失败，没有弹出对话框权限", Toast.LENGTH_SHORT).show();
        }
    }


    public void hangup() {
        DemoLog.i(TAG, "hangup");
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
        sendVideoCallAction(VIDEO_CALL_ACTION_HANGUP, mOnlineCall);
    }

    private void enterRoom() {
        final Intent intent = new Intent(context, TRTCActivity.class);
        intent.putExtra(TRTCActivity.KEY_ROOM_ID, mOnlineCall.room_id);
        activityContext.startActivity(intent);
    }

    //呼叫的用户进房
    private void enterAudioRoom() {


        final Intent intent = new Intent(context, AudioCallMainActivity.class);
        intent.putExtra(Constants.ROOM_ID, mOnlineCall.room_id);

        intent.putExtra(Constants.USER_ID, mOnlineCall.othersUserId);
        intent.putExtra(Constants.USER_NAME, mOnlineCall.othersUserName);
        intent.putExtra(Constants.USER_PORTRAIT, mOnlineCall.othersUserPortrait);
        activityContext.startActivity(intent);
    }

    //被呼叫用户进房
    private void othersEnterAudioRoom() {
        final Intent intent = new Intent(context, AudioCallMainActivity.class);
        intent.putExtra(Constants.ROOM_ID, mOnlineCall.room_id);

        intent.putExtra(Constants.USER_ID, mOnlineCall.getUserId());
        intent.putExtra(Constants.USER_NAME, mOnlineCall.getUserName());
        intent.putExtra(Constants.USER_PORTRAIT, mOnlineCall.getUserPortrait());
        activityContext.startActivity(intent);
    }


    public void sendVideoCallAction(int action, CustomMessage roomInfo) {
        DemoLog.i(TAG, "发送视频sendVideoCallAction action: " + action
                + " call_id: " + roomInfo.call_id
                + " room_id: " + roomInfo.room_id
                + " partner: " + roomInfo.getPartner());
        Gson gson = new Gson();
        CustomMessage message = new CustomMessage();
        message.version = JSON_VERSION_3_ANDROID_IOS_TRTC;
        message.call_id = roomInfo.call_id;
        message.room_id = roomInfo.room_id;
        message.action = action;

        message.invited_list = roomInfo.invited_list;

        message.isAudioCall = roomInfo.isAudioCall;
        message.userId = roomInfo.userId;
        message.userPortrait = roomInfo.userPortrait;
        message.userName = roomInfo.userName;


        message.othersUserId = roomInfo.othersUserId;
        message.othersUserPortrait = roomInfo.othersUserPortrait;
        message.othersUserName = roomInfo.othersUserName;


        if (action == VIDEO_CALL_ACTION_HANGUP) {
            message.duration = (int) (System.currentTimeMillis() - mEnterRoomTime + 500) / 1000;
        }
        String data = gson.toJson(message);
        DemoLog.w(TAG, "stringJson" + data);
        MessageInfo info = MessageInfoUtil.buildCustomMessage(data);
        if (TextUtils.equals(mOnlineCall.getPartner(), roomInfo.getPartner())) {
            TUIKitLog.w("ccc发送通话邀请", data);
            //mUISender.sendMessage(info, false);

            TUIKitLog.w("ccc发送的自定义消息", data);
            TUIKitLog.w("ccc创建回话", roomInfo.getPartner());
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, roomInfo.getPartner());
            con.sendMessage(info.getTIMMessage(), new TIMValueCallBack<TIMMessage>() {

                @Override
                public void onError(int code, String desc) {
                    DemoLog.i(TAG, "sendMessage fail:" + code + "=" + desc);
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {
                    TUIKitLog.i(TAG, "sendMessage onSuccess---------");
                }
            });


        } else {
            TUIKitLog.w("发送的自定义消息", data);
            TUIKitLog.w("创建回话", roomInfo.getPartner());
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, roomInfo.getPartner());
            con.sendMessage(info.getTIMMessage(), new TIMValueCallBack<TIMMessage>() {

                @Override
                public void onError(int code, String desc) {
                    DemoLog.i(TAG, "sendMessage fail:" + code + "=" + desc);
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {
                    TUIKitLog.i(TAG, "sendMessage onSuccess---------");
                }
            });
        }
    }

    private String createCallID() {
        final String CHARS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(GenerateTestUserSig.SDKAPPID).append("-").append(TIMManager.getInstance().getLoginUser()).append("-");
        for (int i = 0; i < 32; i++) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

    //组装视频电话
    private void assembleOnlineCall(CustomMessage roomInfo) {
        mOnlineCall = new CustomMessage();
        if (roomInfo == null) {
            mOnlineCall.call_id = createCallID();
            mOnlineCall.room_id = new Random().nextInt();
            //对方用户ID
            mOnlineCall.invited_list = new String[]{"1234567"};
            // DemoLog.w(TAG, "是否是用户ID------" + mUISender.getChatInfo().getId());
            //对方用户ID
            mOnlineCall.setPartner("1234567");
        } else {

            mOnlineCall = roomInfo;
        }
    }

    //组装视频电话
    private void assembleOnlineCall(CustomMessage roomInfo, String listeningUserId) {
        mOnlineCall = new CustomMessage();
        if (roomInfo == null) {
            mOnlineCall.call_id = createCallID();
            mOnlineCall.room_id = new Random().nextInt();
            //对方用户ID
            mOnlineCall.invited_list = new String[]{listeningUserId};
            // DemoLog.w(TAG, "是否是用户ID------" + mUISender.getChatInfo().getId());
            //对方用户ID
            mOnlineCall.setPartner(listeningUserId);

        } else {
            DemoLog.w(TAG, "组装视频电话有数据------22222");
            mOnlineCall.call_id = roomInfo.call_id;
            mOnlineCall.room_id = roomInfo.room_id;
            mOnlineCall.invited_list = roomInfo.invited_list;
            mOnlineCall.setPartner(roomInfo.getPartner());
        }
    }


    //组装语音电话
    private void assembleOnlineCall(CustomMessage roomInfo, String userId,
                                    String userPortrait,
                                    String userName,

                                    String othersUserId,
                                    String othersUserPortrait,
                                    String othersUserName,
                                    int roomId
            , boolean isAudioCall) {
        mOnlineCall = new CustomMessage();
        if (roomInfo == null) {
            mOnlineCall.call_id = createCallID();
            mOnlineCall.room_id = roomId;
            //对方用户ID
            mOnlineCall.invited_list = new String[]{othersUserId};
            // DemoLog.w(TAG, "是否是用户ID------" + mUISender.getChatInfo().getId());
            //对方用户ID
            mOnlineCall.setPartner(othersUserId);

            //
            mOnlineCall.setUserId(userId);
            mOnlineCall.setUserPortrait(userPortrait);
            mOnlineCall.setUserName(userName);

            mOnlineCall.setOthersUserId(othersUserId);
            mOnlineCall.setOthersUserPortrait(othersUserPortrait);
            mOnlineCall.setOthersUserName(othersUserName);
            mOnlineCall.setAudioCall(isAudioCall);

        } else {
            DemoLog.w(TAG, "组装视频电话有数据------22222");
            mOnlineCall.call_id = roomInfo.call_id;
            mOnlineCall.room_id = roomInfo.room_id;
            mOnlineCall.invited_list = roomInfo.invited_list;
            mOnlineCall.setPartner(roomInfo.getPartner());


            mOnlineCall.setUserId(roomInfo.getUserId());
            mOnlineCall.setUserPortrait(roomInfo.getUserPortrait());
            mOnlineCall.setUserName(roomInfo.getUserName());

            mOnlineCall.setOthersUserId(roomInfo.getOthersUserId());
            mOnlineCall.setOthersUserPortrait(roomInfo.getOthersUserPortrait());
            mOnlineCall.setOthersUserName(roomInfo.getOthersUserName());
            mOnlineCall.setAudioCall(roomInfo.isAudioCall());

        }
    }

    public void onNewMessage(List<TIMMessage> msgs) {
        CustomMessage data = CustomMessage.convert2VideoCallData(msgs);
        if (data != null) {
            onNewComingCall(data);
        }
    }

    private void onNewComingCall(CustomMessage message) {
        DemoLog.i(TAG, "onNewComingCall current state: " + mCurrentVideoCallStatus
                + " call_id action: " + message.action
                + " coming call_id: " + message.call_id
                + " coming room_id: " + message.room_id
                + " current room_id: " + (mOnlineCall == null ? null : mOnlineCall.room_id));
        DemoLog.i(TAG, "messageaction" + message.action + "");
        if (message.isAudioCall()) {
            switch (message.action) {


                case VIDEO_CALL_ACTION_DIALING:

                    if (mCurrentVideoCallStatus == VIDEO_CALL_STATUS_FREE) {
                        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_WAITING;
                        //创建一个会话
                        // startC2CConversation(message);
                        assembleOnlineCall(message);
                        showCallDialog();
                        DemoLog.w(TAG, "---VIDEO_CALL_ACTION_DIALING");
                    } else {
                        sendVideoCallAction(VIDEO_CALL_ACTION_LINE_BUSY, message);
                    }
                    break;
                case VIDEO_CALL_ACTION_SPONSOR_CANCEL:
                    EjuImController.getInstance().updatTalk(mOnlineCall.room_id + "", "3");
                    DemoLog.w(TAG, "---VIDEO_CALL_ACTION_SPONSOR_CANCEL");
                    if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE && TextUtils.equals(message.call_id, mOnlineCall.call_id)) {
                        dismissDialog();
                        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                    }
                    break;
                case VIDEO_CALL_ACTION_REJECT:
                    EjuImController.getInstance().updatTalk(mOnlineCall.room_id + "", "6");
                    DemoLog.w(TAG, "---VIDEO_CALL_ACTION_REJECT");
                    if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE && TextUtils.equals(message.call_id, mOnlineCall.call_id)) {
                        dismissDialog();
                        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                    }
                    break;
                case VIDEO_CALL_ACTION_SPONSOR_TIMEOUT:
                    EjuImController.getInstance().updatTalk(mOnlineCall.room_id + "", "4");
                    DemoLog.w(TAG, "---VIDEO_CALL_ACTION_SPONSOR_TIMEOUT");
                    if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE && TextUtils.equals(message.call_id, mOnlineCall.call_id)) {
                        dismissDialog();
                        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                    }
                    break;
                case VIDEO_CALL_ACTION_ACCEPTED:
                    EjuImController.getInstance().updatTalk(mOnlineCall.room_id + "", "5");
                    DemoLog.w(TAG, "---VIDEO_CALL_ACTION_ACCEPTED");
                    if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE && TextUtils.equals(message.call_id, mOnlineCall.call_id)) {
                        dismissDialog();
                    }

                    assembleOnlineCall(message);
                    enterAudioRoom();

                    break;
                case VIDEO_CALL_ACTION_HANGUP:
                    EjuImController.getInstance().updatTalk(mOnlineCall.room_id + "", "7");
                    DemoLog.w(TAG, "---VIDEO_CALL_ACTION_HANGUP");
                    dismissDialog();
                    mTRTCCloud.exitRoom();
                    mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                    break;
                case VIDEO_CALL_ACTION_LINE_BUSY:
                    EjuImController.getInstance().updatTalk(mOnlineCall.room_id + "", "6");
                    DemoLog.w(TAG, "---VIDEO_CALL_ACTION_LINE_BUSY");
                    if (mCurrentVideoCallStatus == VIDEO_CALL_STATUS_BUSY && TextUtils.equals(message.call_id, mOnlineCall.call_id)) {
                        dismissDialog();
                    }
                    break;
                default:
                    DemoLog.e(TAG, "unknown data.action: " + message.action);
                    break;
            }
        }
    }

    private void startC2CConversation(CustomMessage message) {
        // 小米手机需要在安全中心里面把demo的"后台弹出权限"打开，才能当应用退到后台时弹出通话请求对话框。
//        DemoLog.i(TAG, "startC2CConversation " + message.getPartner());
//        ChatInfo chatInfo = new ChatInfo();
//        chatInfo.setType(TIMConversationType.C2C);
//        chatInfo.setId(message.getPartner());
//        chatInfo.setChatName(message.getPartner());
//        Intent intent = new Intent(DemoApplication.instance(), ChatActivity.class);
//        intent.putExtra(Constants.CHAT_INFO, chatInfo);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        DemoApplication.instance().startActivity(intent);
    }


    public void showCallDialog() {
        DemoLog.i(TAG, "showCallDialog: ");

        if (mCurrentVideoCallStatus == VIDEO_CALL_STATUS_WAITING) {
            boolean success = showIncomingDialingDialog();
            if (success) {
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
            } else {
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                sendVideoCallAction(VIDEO_CALL_ACTION_REJECT, mOnlineCall);
                Toast.makeText(context, "发起通话失败，没有弹出对话框权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean showIncomingDialingDialog() {
        dismissDialog();
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mVideoCallIncomingTimeOut, VIDEO_CALL_OUT_INCOMING_TIME_OUT);
        mDialog = new TRTCDialog(activityContext);
        mDialog.setTitle("来电话了");
        mDialog.setPositiveButton("接听", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoLog.i(TAG, "VIDEO_CALL_ACTION_ACCEPTED");
                mHandler.removeCallbacksAndMessages(null);
                sendVideoCallAction(VIDEO_CALL_ACTION_ACCEPTED, mOnlineCall);
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
                othersEnterAudioRoom();
            }
        });
        mDialog.setNegativeButton("拒绝", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoLog.i(TAG, "VIDEO_CALL_ACTION_REJECT");
                mHandler.removeCallbacksAndMessages(null);
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                sendVideoCallAction(VIDEO_CALL_ACTION_REJECT, mOnlineCall);
            }
        });
        return mDialog.showSystemDialog();
    }

    private boolean showOutgoingDialingDialog() {
        dismissDialog();
        mDialog = new TRTCDialog(activityContext);
        mDialog.setTitle("等待对方接听");
        mDialog.setPositiveButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoLog.i(TAG, "VIDEO_CALL_ACTION_SPONSOR_CANCEL");
                mHandler.removeCallbacksAndMessages(null);
                sendVideoCallAction(VIDEO_CALL_ACTION_SPONSOR_CANCEL, mOnlineCall);

            }
        });
        return mDialog.showSystemDialog();
    }

    private void dismissDialog() {
        mHandler.removeCallbacksAndMessages(null);
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

}
