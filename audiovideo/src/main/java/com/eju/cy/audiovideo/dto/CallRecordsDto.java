package com.eju.cy.audiovideo.dto;

import java.util.List;

public class CallRecordsDto {


    /**
     * msg : 成功
     * data : {"records":[{"room_status":3,"create_time":"2020-03-10 16:07:01","initiative":false,"listening":1,"begin_time":"","room_status_des":"房间正在使用中【房间非空 至少一个人】","talk_status":2,"calling":6789,"room_id":"20200310160701116789","modify_time":"2020-03-10 16:07:23","talk_status_des":"呼叫中","end_time":""}]}
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
        private List<RecordsBean> records;

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public static class RecordsBean {
            /**
             * room_status : 3
             * create_time : 2020-03-10 16:07:01
             * initiative : false
             * listening : 1
             * begin_time :
             * room_status_des : 房间正在使用中【房间非空 至少一个人】
             * talk_status : 2
             * calling : 6789
             * room_id : 20200310160701116789
             * modify_time : 2020-03-10 16:07:23
             * talk_status_des : 呼叫中
             * end_time :
             */

            private int room_status;
            private String create_time;
            private boolean initiative;
            private int listening;
            private String begin_time;
            private String room_status_des;
            private int talk_status;
            private int calling;
            private String room_id;
            private String modify_time;
            private String talk_status_des;
            private String end_time;

            private  int id;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getRoom_status() {
                return room_status;
            }

            public void setRoom_status(int room_status) {
                this.room_status = room_status;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public boolean isInitiative() {
                return initiative;
            }

            public void setInitiative(boolean initiative) {
                this.initiative = initiative;
            }

            public int getListening() {
                return listening;
            }

            public void setListening(int listening) {
                this.listening = listening;
            }

            public String getBegin_time() {
                return begin_time;
            }

            public void setBegin_time(String begin_time) {
                this.begin_time = begin_time;
            }

            public String getRoom_status_des() {
                return room_status_des;
            }

            public void setRoom_status_des(String room_status_des) {
                this.room_status_des = room_status_des;
            }

            public int getTalk_status() {
                return talk_status;
            }

            public void setTalk_status(int talk_status) {
                this.talk_status = talk_status;
            }

            public int getCalling() {
                return calling;
            }

            public void setCalling(int calling) {
                this.calling = calling;
            }

            public String getRoom_id() {
                return room_id;
            }

            public void setRoom_id(String room_id) {
                this.room_id = room_id;
            }

            public String getModify_time() {
                return modify_time;
            }

            public void setModify_time(String modify_time) {
                this.modify_time = modify_time;
            }

            public String getTalk_status_des() {
                return talk_status_des;
            }

            public void setTalk_status_des(String talk_status_des) {
                this.talk_status_des = talk_status_des;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
            }
        }
    }
}
