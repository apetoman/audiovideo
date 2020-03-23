package com.eju.cy.audiovideo.im.entrance;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.GenerateTestUserSig;
import com.eju.cy.audiovideo.dto.RoomDto;
import com.eju.cy.audiovideo.dto.UpdateStatusDto;
import com.eju.cy.audiovideo.im.helper.CustomAVCallUIController;
import com.eju.cy.audiovideo.im.helper.ImConfigHelper;
import com.eju.cy.audiovideo.im.utils.Constants;
import com.eju.cy.audiovideo.im.utils.DemoLog;
import com.eju.cy.audiovideo.net.AppNetInterface;
import com.eju.cy.audiovideo.net.RetrofitManager;
import com.eju.cy.audiovideo.utils.ParameterUtils;
import com.eju.cy.audiovideo.utils.TypeState;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.session.SessionWrapper;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IMEventListener;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * IM 控制类
 *
 * @ Name: Caochen
 * @ Date: 2020-03-20
 * @ Time: 10:16
 * @ Description：
 */
public class EjuImController {
    private static final String TAG = EjuImController.class.getSimpleName();
    private static EjuImController mEjuImController;
    private WeakReference<Application> mWeakReference;
    private Application application;

    private String mUserToken, mUserId;
    int mRoomId = 0;

    //单例
    public static EjuImController getInstance() {

        if (mEjuImController == null) {
            synchronized (EjuImController.class) {
                if (mEjuImController == null) {
                    mEjuImController = new EjuImController();
                }
            }

        }
        return mEjuImController;
    }


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-20
     * @ Time: 10:36
     * @ Description：
     * 初始化SDK
     */
    public void initSDK(final Application application) {
        this.mWeakReference = new WeakReference<Application>(application);
        this.application = mWeakReference.get();
        if (SessionWrapper.isMainProcess(application)) {
            TUIKit.init(application, GenerateTestUserSig.SDKAPPID, new ImConfigHelper().getConfigs(application));
            application.registerActivityLifecycleCallbacks(new StatisticActivityLifecycleCallback(application));
        }

        CustomAVCallUIController.getInstance(application).onCreate(application);

        //消息监听
        IMEventListener imEventListener = new IMEventListener() {
            @Override
            public void onNewMessages(List<TIMMessage> msgs) {
                DemoLog.i(TAG, "onNewMessages");
                CustomAVCallUIController.getInstance(application).onNewMessage(msgs);
            }
        };
        TUIKit.addIMEventListener(imEventListener);
    }


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-20
     * @ Time: 10:51
     * @ Description：
     * 用户登录IM
     */
    public void loginSDK(final String userId, String userSig, final String userToken, final EjuImSdkCallBack ejuImSdkCallBack) {


        TUIKit.login(userId, userSig, new IUIKitCallBack() {
            @Override
            public void onError(String module, final int code, final String desc) {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        ToastUtil.toastLongMessage("登录失败, errCode = " + code + ", errInfo = " + desc);
//                    }
//                });
//                DemoLog.i(TAG, "imLogin errorCode = " + code + ", errorInfo = " + desc);

                ejuImSdkCallBack.onError(module, code, desc);
            }

            @Override
            public void onSuccess(Object data) {

                ejuImSdkCallBack.onSuccess(data);


                mUserId = userId;
                mUserToken = userToken;

                SharedPreferences shareInfo = application.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shareInfo.edit();
                editor.putBoolean(Constants.AUTO_LOGIN, true);
                editor.commit();

//                ToastUtils.showLong("登录成功");
//                SharedPreferences shareInfo = getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = shareInfo.edit();
//                editor.putBoolean(Constants.AUTO_LOGIN, true);
//                editor.commit();
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                if (userId == "211425") {
//                    intent.putExtra(Constants.LISTENING_USER_ID, "9027");
//                } else {
//                    intent.putExtra(Constants.LISTENING_USER_ID, "211425");
//                }
//
//
//                startActivity(intent);
//                finish();
            }
        });


    }

    /**
     * @ Name: Caochen
     * @ Date: 2020-03-20
     * @ Time: 10:58
     * @ Description：
     * 设置应用APP MainActivity
     */
    public void setAppMainActivity(Activity mainActivity) {

        CustomAVCallUIController.getInstance().setActivityContext(mainActivity);

    }

    /**
     * @ Name: Caochen
     * @ Date: 2020-03-20
     * @ Time: 11:00
     * @ Description： 创建语音通话请求
     */
    @SuppressLint("CheckResult")
    public void createAudioCallRequest(

            final String userId,
            final String userToken,
            final String type,
            final String userPortrait,
            final String userName,

            final String othersUserId,
            final String othersUserPortrait,
            final String othersUserName,

            final boolean isAudioCall) {


        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application, userId, userToken, type);

        // String roomID = "0";
        httpInterface.getRoom(
                ParameterUtils.prepareFormData("1"),
                ParameterUtils.prepareFormData(userId),
                ParameterUtils.prepareFormData(othersUserId),
                ParameterUtils.prepareFormData("1")
        ).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<RoomDto, ObservableSource<UpdateStatusDto>>() {
                    @Override
                    public ObservableSource<UpdateStatusDto> apply(RoomDto roomDto) throws Exception {

                        mRoomId = roomDto.getData().getId();

                        return httpInterface.updateTalkStatus(ParameterUtils.prepareFormData(roomDto.getData().getId() + ""), ParameterUtils.prepareFormData("1"));


                    }
                }).observeOn(Schedulers.io())
                .flatMap(new Function<UpdateStatusDto, ObservableSource<UpdateStatusDto>>() {
                    @Override
                    public ObservableSource<UpdateStatusDto> apply(UpdateStatusDto o) throws Exception {
                        return httpInterface.updateTalkStatus(ParameterUtils.prepareFormData(mRoomId + ""), ParameterUtils.prepareFormData("2"));
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateStatusDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {


                    }

                    @Override
                    public void onNext(UpdateStatusDto updateStatusDto) {
                        if (null != updateStatusDto && "10000".equals(updateStatusDto.getCode())) {
                            CustomAVCallUIController.getInstance().createVideoCallRequest(userId, userPortrait, userName, othersUserId, othersUserPortrait, othersUserName, mRoomId, isAudioCall);
                        } else {
                            ToastUtils.showLong("请稍后再试"+updateStatusDto.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.w("错误"+e.toString());
                        ToastUtils.showLong("请稍后再试222");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
//
//
//        httpInterface.getRoom(
//                ParameterUtils.prepareFormData("1"),
//                ParameterUtils.prepareFormData(userId),
//                ParameterUtils.prepareFormData(othersUserId),
//                ParameterUtils.prepareFormData("1")
//        ).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<RoomDto>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(RoomDto roomDto) {
//                        if (null != roomDto && "10000".equals(roomDto.getCode())) {
//                            LogUtils.w("房间号是" + roomDto.getData().getId());
//
//                            updatTalk(roomDto.getData().getId() + "", "1");
//                            CustomAVCallUIController.getInstance().createVideoCallRequest(userId, userPortrait, userName, othersUserId, othersUserPortrait, othersUserName, roomDto.getData().getId(), isAudioCall);
//
//
//                        } else {
//                            LogUtils.w("失败" + roomDto.getMsg() + "---" + roomDto.getCode());
//                        }
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtils.w("失败" + e.toString());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });


    }


    //修改房间状态
    public void updatTalk(String roomId, String room_talk) {

        AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application, mUserId, mUserToken, TypeState.JDM);
        httpInterface.updateTalkStatus(ParameterUtils.prepareFormData(roomId),
                ParameterUtils.prepareFormData(room_talk)
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateStatusDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UpdateStatusDto updateStatusDto) {
                        if (null != updateStatusDto && "10000".equals(updateStatusDto.getCode())) {
                            LogUtils.w("修改房间状态" + updateStatusDto.getMsg());
                        } else {
                            LogUtils.w("失败" + updateStatusDto.getMsg() + "---" + updateStatusDto.getCode());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


}
