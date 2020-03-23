package com.eju.cy.audiovideo.utils;

import android.content.Context;


/**
* @ Name: Caochen
* @ Date: 2020-02-25
* @ Time: 10:53
* @ Description： 屏幕相关工具类
*/
public class ScreenUtils {

    /**
     * 获取屏幕宽度（px）
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


    /**
     * 获取屏幕高度（px）
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
