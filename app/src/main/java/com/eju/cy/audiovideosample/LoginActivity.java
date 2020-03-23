package com.eju.cy.audiovideosample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eju.cy.audiovideo.GenerateTestUserSig;
import com.eju.cy.audiovideo.im.entrance.EjuImController;
import com.eju.cy.audiovideo.im.entrance.EjuImSdkCallBack;
import com.eju.cy.audiovideo.im.utils.Constants;
import com.eju.cy.audiovideo.im.utils.DemoLog;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvZs, mTvLs, mTvLogin;
    private String userId, userToken, calling, listening;

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQ_PERMISSION_CODE = 0x100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTvZs = (TextView) findViewById(R.id.tv_zs);
        mTvLs = (TextView) findViewById(R.id.tv_ls);
        mTvLogin = (TextView) findViewById(R.id.tv_login);



        mTvZs.setOnClickListener(this);
        mTvLs.setOnClickListener(this);
        mTvLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_zs:
                mTvZs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mTvLs.setBackgroundColor(getResources().getColor(R.color.white));

                userId = "9027";
                userToken = "76961c79aadd50a89a4d5d452be1fdb9b75b53ed";
                calling = "9027";
                listening = "211425";


                break;
            case R.id.tv_ls:
                mTvZs.setBackgroundColor(getResources().getColor(R.color.white));
                mTvLs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                userId = "211425";
                userToken = "7e1450e5b8b44eeeda88cb5cdd1186d0b85311d0";
                calling = "211425";
                listening = "9027";

                break;
            case R.id.tv_login:

//                String userSig = GenerateTestUserSig.genTestUserSig(userId);
//                TUIKit.login(userId, userSig, new IUIKitCallBack() {
//                    @Override
//                    public void onError(String module, final int code, final String desc) {
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                ToastUtil.toastLongMessage("登录失败, errCode = " + code + ", errInfo = " + desc);
//                            }
//                        });
//                        DemoLog.i(TAG, "imLogin errorCode = " + code + ", errorInfo = " + desc);
//                    }
//
//                    @Override
//                    public void onSuccess(Object data) {
//
//                        ToastUtils.showLong("登录成功");
//                        SharedPreferences shareInfo = getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = shareInfo.edit();
//                        editor.putBoolean(Constants.AUTO_LOGIN, true);
//                        editor.commit();
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        if (userId == "211425") {
//                            intent.putExtra(Constants.LISTENING_USER_ID, "9027");
//                        } else {
//                            intent.putExtra(Constants.LISTENING_USER_ID, "211425");
//                        }
//
//
//                        startActivity(intent);
//                        finish();
//                    }
//                });

                String userSig = GenerateTestUserSig.genTestUserSig(userId);
                EjuImController.getInstance().loginSDK(userId, userSig,userToken, new EjuImSdkCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        if (userId == "211425") {
                            intent.putExtra(Constants.LISTENING_USER_ID, "9027");
                        } else {
                            intent.putExtra(Constants.LISTENING_USER_ID, "211425");
                        }

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        DemoLog.i(TAG, "imLogin errorCode = " + errCode + ", errorInfo = " + errMsg);
                    }
                });

                break;

        }

    }

    //权限检查
    public static boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                String[] permissionsArray = permissions.toArray(new String[1]);
                ActivityCompat.requestPermissions(activity,
                        permissionsArray,
                        REQ_PERMISSION_CODE);
                return false;
            }
        }

        return true;
    }

    /**
     * 系统请求权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.toastLongMessage("未全部授权，部分功能可能无法使用！");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
