package com.eju.cy.audiovideo.utils;






import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author: zengmei
 * @description: json转换工具类
 **/
public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    /**
     * 解析StringTo实体类
     */
    @Nullable
    public static <T> T fromJson(String json, Class<T> cls) {
        if (json == null) {
            return null;
        }
        try {
            return new Gson().fromJson(json, cls);
        } catch (Exception ignored) {

            return null;
        }
    }

    /**
     * 转化Json为集合
     * type listType = new TypeToken<ArrayList<YourClass>>(){}.getType();
     * List<YourClass> yourClassList = new Gson().fromJson(jsonArray, listType);
     */
    @Nullable
    public static List<?> fromJson(String json, Type type) {
        if (json == null) {
            return null;
        }
        try {
            return new Gson().fromJson(json, type);
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * 转化列表为JSON
     */
    @Nullable
    public static <T> String toJson(final List<T> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        try {
            return new Gson().toJson(list);
        } catch (Exception ignored) {

            return "";
        }
    }


    /**
     * 转化实体类为JSON字符串
     */
    @Nullable
    public static <T> String toJson(T bean) {
        if (bean == null) {
            return "";
        }
        try {
            return new Gson().toJson(bean);
        } catch (Exception e) {

            return "";
        }
    }



}
