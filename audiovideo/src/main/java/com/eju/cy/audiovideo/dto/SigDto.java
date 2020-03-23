package com.eju.cy.audiovideo.dto;

public class SigDto {


    /**
     * msg : 成功
     * data : {"userSig":"eJyrVgrxCdYrzkxXslJQSgqKcHIxLCnXd9TODfELMjDTjzBwy3AP9UtyNC1PNDRNNs0uS3J1tYhKt7BV0lEAa02tKMgsSgXqNjMwsTAwgIoWp2QnFhRkpgDFDU0MDIyNjCzhcsssaCTDA0tTA2MjA2NDKGimempOaVZKZlQjQYKtUCAIeOLyY_","sdkAppId":1400322900}
     * code : 10000
     * encrypt : 0
     */

    private String msg;
    private DataBean data;
    private String code;
    private int encrypt;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    public static class DataBean {
        /**
         * userSig : eJyrVgrxCdYrzkxXslJQSgqKcHIxLCnXd9TODfELMjDTjzBwy3AP9UtyNC1PNDRNNs0uS3J1tYhKt7BV0lEAa02tKMgsSgXqNjMwsTAwgIoWp2QnFhRkpgDFDU0MDIyNjCzhcsssaCTDA0tTA2MjA2NDKGimempOaVZKZlQjQYKtUCAIeOLyY_
         * sdkAppId : 1400322900
         */

        private String userSig;
        private int sdkAppId;

        public String getUserSig() {
            return userSig;
        }

        public void setUserSig(String userSig) {
            this.userSig = userSig;
        }

        public int getSdkAppId() {
            return sdkAppId;
        }

        public void setSdkAppId(int sdkAppId) {
            this.sdkAppId = sdkAppId;
        }
    }
}
