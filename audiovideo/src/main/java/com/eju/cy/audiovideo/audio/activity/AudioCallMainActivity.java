package com.eju.cy.audiovideo.audio.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.eju.cy.audiovideo.GenerateTestUserSig;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.im.helper.CustomAVCallUIController;
import com.eju.cy.audiovideo.im.helper.TRTCListener;
import com.eju.cy.audiovideo.im.utils.Constants;
import com.eju.cy.audiovideo.im.utils.DemoLog;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import java.util.ArrayList;

import static com.eju.cy.audiovideo.im.helper.CustomMessage.VIDEO_CALL_ACTION_ACCEPTED;

public class AudioCallMainActivity extends Activity {
    private static final String TAG = AudioCallMainActivity.class.getName();

    /**
     * 重要信息
     */
    private int mRoomId;
    private String mUserId;
    private TRTCCloud mTRTCCloud;
    /**
     * 界面元素
     */

    // private Toolbar mToolbar;
    private AppCompatImageButton mMicBtn;
    private AppCompatImageButton mAudioBtn;
    private AppCompatImageButton mExitBtn;

    private ImageView mIvPrtrait;
    private TextView mTvUserName;

    /**
     * 用于监听TRTC事件
     */
    private TRTCCloudListener mTRTCCloudListener = new TRTCCloudListener() {
        @Override
        public void onEnterRoom(long result) {
            if (result == 0) {
                ToastUtils.showShort("进房成功");
            }
        }

        @Override
        public void onExitRoom(int i) {
            super.onExitRoom(i);
            finish();
        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            ToastUtils.showLong("进房失败: " + errMsg);
            LogUtils.w("失败" + errMsg + "" + errCode);
            finish();
        }

        @Override
        public void onRemoteUserEnterRoom(String userId) {
//            TRTCAudioLayout layout = mLayoutManagerTrtc.allocAudioCallLayout(userId);
//            layout.setUserId(userId);
//            layout.setBitmap(Utils.getAvatar(userId));
        }

        @Override
        public void onRemoteUserLeaveRoom(String userId, int reason) {
            // mLayoutManagerTrtc.recyclerAudioCallLayout(userId);

            // 对方超时掉线
            if (reason == 1) {
                finishAudioCall();
                finish();
            }
        }

        @Override
        public void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> userVolumes, int totalVolume) {
            for (TRTCCloudDef.TRTCVolumeInfo info : userVolumes) {
                String userId = info.userId;
                // 如果userId为空，代表自己
//                if (info.userId == null) {
//                    userId = mUserId;
//                }
//                TRTCAudioLayout layout = mLayoutManagerTrtc.findAudioCallLayout(info.userId);
//                if (layout != null) {
//                    layout.setAudioVolume(info.volume);
//                }
            }
        }
    };
    private AppCompatImageButton mHandsfreeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audiocall_activity_main);
        initView();
        initData();
        initListener();
        enterTRTCRoom();
    }

    @Override
    protected void onDestroy() {
        finishAudioCall();
        super.onDestroy();
    }


    private void initListener() {
        mMicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean currentMode = !mMicBtn.isSelected();
                // 开关麦克风
                enableMic(currentMode);
                mMicBtn.setSelected(currentMode);
                if (currentMode) {
                    ToastUtils.showLong("您已开启麦克风");
                    mMicBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_jingyinyikaiqi));
                } else {
                    ToastUtils.showLong("您已关闭麦克风");
                    mMicBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_jingyinweikaiqi));

                }
            }
        });
//        mAudioBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean currentMode = !mAudioBtn.isSelected();
//                // 是否静音
//                enableAudio(currentMode);
//                mAudioBtn.setSelected(currentMode);
//                if (currentMode) {
//                    ToastUtils.showLong("您已取消静音");
//                } else {
//                    ToastUtils.showLong("您已静音");
//                }
//            }
//        });
        mHandsfreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean currentMode = !mHandsfreeBtn.isSelected();
                // 是否走扬声器
                enableHandfree(currentMode);
                mHandsfreeBtn.setSelected(currentMode);
                if (currentMode) {
                    ToastUtils.showLong("使用扬声器模式");
                    mHandsfreeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_miantiyikaiqi));
                } else {
                    ToastUtils.showLong("使用耳机模式");
                    mHandsfreeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_miantiweikaiqi));
                }
            }
        });

        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAudioCall();
                finish();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        mRoomId = intent.getIntExtra(Constants.ROOM_ID, 0);
        mUserId = intent.getStringExtra(Constants.USER_ID);
        String imgUrl = getIntent().getStringExtra(Constants.USER_PORTRAIT);
        String userName = getIntent().getStringExtra(Constants.USER_NAME);
        // mTitleToolbar.setText(getString(R.string.audiocall_title, mRoomId));
        mTRTCCloud = TRTCCloud.sharedInstance(this);
        // 给自己分配一个view
        // mLayoutManagerTrtc.setMySelfUserId(mUserId);
        // TRTCAudioLayout layout = mLayoutManagerTrtc.allocAudioCallLayout(mUserId);

//        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.avatar2_100)).getBitmap();
//        layout.setBitmap(bitmap);

//        layout.setBitmap(ImageUtils.drawable2Bitmap(getResources().getDrawable(R.drawable.avatar2_100)));
//        layout.setUserId(mUserId);

        DemoLog.w(TAG, "头像" + getIntent().getStringExtra(Constants.USER_PORTRAIT));
        // mIvPrtrait.setImageURI(Uri.parse(getIntent().getStringExtra(Constants.USER_PORTRAIT)));


        if (null != imgUrl && imgUrl.length() > 0) {
            Glide.with(this).load(imgUrl).into(mIvPrtrait);
        }

        mTvUserName.setText(userName);
        LogUtils.w("userId----" + mUserId);
        //layout.setBitmap(Utils.getAvatar(mUserId));
        //layout.setUserId(mUserId);
    }

    private void initView() {
//        mTitleToolbar = (TextView) findViewById(R.id.toolbar_title);
        //mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mMicBtn = (AppCompatImageButton) findViewById(R.id.btn_mic);
        mExitBtn = (AppCompatImageButton) findViewById(R.id.btn_exit);
        //  mAudioBtn = (AppCompatImageButton) findViewById(R.id.btn_audio);
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        mIvPrtrait = (ImageView) findViewById(R.id.iv_portrait);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        // mLayoutManagerTrtc = (TRTCAudioLayoutManager) findViewById(R.id.trtc_layout_manager);
        mHandsfreeBtn = (AppCompatImageButton) findViewById(R.id.btn_handsfree);

        //设置选中态
        mMicBtn.setActivated(true);
        //  mAudioBtn.setActivated(true);
        mHandsfreeBtn.setActivated(true);
        mMicBtn.setSelected(true);
        //  mAudioBtn.setSelected(true);
        mHandsfreeBtn.setSelected(true);

    }

    private void enterTRTCRoom() {


        mTRTCCloud.enableAudioVolumeEvaluation(800);
        mTRTCCloud.setListener(mTRTCCloudListener);
        mTRTCCloud.startLocalAudio();
        // 拼接进房参数
        TRTCCloudDef.TRTCParams params = new TRTCCloudDef.TRTCParams();
        params.userSig = GenerateTestUserSig.genTestUserSig(mUserId);
        params.roomId = mRoomId;
        params.sdkAppId = GenerateTestUserSig.SDKAPPID;
        params.role = TRTCCloudDef.TRTCRoleAnchor;
        params.userId = mUserId;


        LogUtils.w("---userSig" + params.userSig);
        LogUtils.w("---参数" + params.roomId);
        LogUtils.w("---sdkAppId" + params.sdkAppId);

        LogUtils.w("---userId" + params.userId);

        mTRTCCloud.enterRoom(params, TRTCCloudDef.TRTC_APP_SCENE_AUDIOCALL);
    }

    private void exitRoom() {

        TRTCListener.getInstance().removeTRTCCloudListener(mTRTCCloudListener);
        mTRTCCloud.exitRoom();
    }

    //启用麦克风
    public void enableMic(boolean enable) {
        if (enable) {
            mTRTCCloud.startLocalAudio();
        } else {
            mTRTCCloud.stopLocalAudio();
        }
    }

    //免提
    public void enableHandfree(boolean isUseHandsfree) {
        mTRTCCloud.setAudioRoute(isUseHandsfree ? TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER :
                TRTCCloudDef.TRTC_AUDIO_ROUTE_EARPIECE);
    }

    //静音
    public void enableAudio(boolean enable) {
        mTRTCCloud.muteAllRemoteAudio(!enable);
    }


    private void finishAudioCall() {
        CustomAVCallUIController.getInstance().hangup();
        mTRTCCloud.exitRoom();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DemoLog.i(TAG, "onBackPressed");
        finishAudioCall();
    }

//    private void enterRoom() {
//        TRTCListener.getInstance().addTRTCCloudListener(mTRTCCloudListener);
//        mTRTCCloud.setListener(TRTCListener.getInstance());
//        mTRTCCloud.startLocalAudio();
//        mTRTCCloud.startLocalPreview(true, mLocalPreviewView);
//        mTRTCCloud.enterRoom(mTRTCParams, TRTC_APP_SCENE_VIDEOCALL);
//    }
}
