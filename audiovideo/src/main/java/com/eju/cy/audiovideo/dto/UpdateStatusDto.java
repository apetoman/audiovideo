package com.eju.cy.audiovideo.dto;

public class UpdateStatusDto {


    /**
     * msg : 房间状态 创建成功 -->更新房间状态为使用中或者释放！
     * code : 10000
     * encrypt : 0
     */

    private String msg;
    private String code;
    private int encrypt;
    private int roomId;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
}
