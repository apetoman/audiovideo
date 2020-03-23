package com.eju.cy.audiovideo.utils;

import android.text.TextUtils;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.annotations.Nullable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Cc on 2018/5/1.
 */

public class ParameterUtils {

    @Nullable
  public   static RequestBody prepareFormData(String value) {
        if(TextUtils.isEmpty(value)) {
            return null;
        }
        return RequestBody.create(MultipartBody.FORM, value);
    }

    @Nullable
    public   static Map<String, String> prepareBodyData(String key, String value) {
        Map<String, String> map=new HashMap<>();
        map.put(key,value);
        return map;
    }


    static MultipartBody.Part prepareFileData(String paramName, URI fileUri) {
        File file = new File(fileUri);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/octet-stream"),
                file
        );
        return MultipartBody.Part.createFormData(paramName, file.getName(), body);
    }
}
