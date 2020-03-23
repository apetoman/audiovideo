package com.eju.cy.audiovideo.dto;

public class RoomDto {


    /**
     * msg : 成功
     * data : {"room_id":"20200310143404111","id":3}
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
         * room_id : 20200310143404111
         * id : 3
         */

        private int room_id;
        private int id;

        public int getRoom_id() {
            return room_id;
        }

        public void setRoom_id(int room_id) {
            this.room_id = room_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
