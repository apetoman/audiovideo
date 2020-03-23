package com.eju.cy.audiovideo.im.entrance;

public interface EjuImSdkCallBack {

    void onSuccess(Object data);

    void onError(String module, int errCode, String errMsg);
}
