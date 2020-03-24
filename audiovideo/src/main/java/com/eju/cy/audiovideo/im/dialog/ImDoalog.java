package com.eju.cy.audiovideo.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eju.cy.audiovideo.R;

public class ImDoalog {

    private Context mContext;
    protected Dialog dialog;
    private RelativeLayout mBackgroundLayout;
    private AppCompatImageButton iv_guanduan, iv_jieting;
    private ImageView iv_portrait;
    private TextView tv_user_name;

    private Display mDisplay;
    /**
     * 是否显示title
     */
    private boolean showTitle = false;
    /***
     * 是否显示确定按钮
     */
    private boolean showPosBtn = false;

    /**
     * 是否显示取消按钮
     */
    private boolean showNegBtn = false;

    /**
     * dialog  宽度
     */
    private float dialogWidth = 1f;


    public ImDoalog(Context context) {
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
    }


    public ImDoalog builder() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.im_dialoglayout, null);

        mBackgroundLayout = (RelativeLayout) view.findViewById(R.id.ll_background);
        iv_guanduan = (AppCompatImageButton) view.findViewById(R.id.btn_guaduan);
        iv_jieting = (AppCompatImageButton) view.findViewById(R.id.btn_jieting);

        iv_portrait = (ImageView) view.findViewById(R.id.iv_portrait);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);

        dialog = new Dialog(mContext, R.style.TUIKit_AlertDialogStyle);
        dialog.setContentView(view);
        mBackgroundLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (mDisplay.getWidth() * dialogWidth), WindowManager.LayoutParams.MATCH_PARENT));

        return this;
    }


    /**
     * 设置确定
     *
     * @param listener
     * @return
     */
    public ImDoalog setPositiveButton(
            final View.OnClickListener listener) {
        showPosBtn = true;

        iv_jieting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }


    /***
     * 设置取消
     * @param listener
     * @return
     */
    public ImDoalog setNegativeButton(
            final View.OnClickListener listener) {
        showNegBtn = true;

        iv_guanduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }


    /***
     * 是否点击返回能够取消
     * @param cancel
     * @return
     */
    public ImDoalog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }


    /**
     * 设置是否可以取消
     *
     * @param isCancelOutside
     * @return
     */
    public ImDoalog setCancelOutside(boolean isCancelOutside) {
        dialog.setCanceledOnTouchOutside(isCancelOutside);
        return this;
    }

    /**
     * 获取确定按钮
     *
     * @return
     */
    public AppCompatImageButton getBtn_neg() {
        return iv_jieting;
    }


    /***
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     * @param dpValue
     * @return
     */
    public int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public int px2sp(float pxValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 设置dialog  宽度
     *
     * @param dialogWidth
     * @return
     */
    public ImDoalog setDialogWidth(float dialogWidth) {
        if (mBackgroundLayout != null) {
            mBackgroundLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (mDisplay.getWidth() * dialogWidth), LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        this.dialogWidth = dialogWidth;
        return this;
    }


    /**
     * 获取取消按钮
     *
     * @return
     */
    public AppCompatImageButton AppCompatImageButton() {
        return iv_guanduan;
    }

    public void show() {

        dialog.show();
    }


    public void setPortrait(String url) {
        Glide.with(mContext).load(url).into(iv_portrait);
    }

    public void setUserName(String userName) {
        tv_user_name.setText(userName + "");
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
