package com.eju.cy.audiovideosample;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.im.entrance.EjuImController;

public class DemoApplication extends Application {

    private static final String TAG = DemoApplication.class.getSimpleName();

    private static DemoApplication instance;

    public static DemoApplication instance() {
        return instance;
    }

    @Override
    public void onCreate() {


        LogUtils.i(TAG, "onCreate");
        super.onCreate();
        instance = this;

        //初始化SDK
        EjuImController.getInstance().initSDK(this);


    }


}
