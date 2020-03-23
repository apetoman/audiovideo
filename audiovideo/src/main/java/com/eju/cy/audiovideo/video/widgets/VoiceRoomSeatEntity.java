package com.eju.cy.audiovideo.video.widgets;

public class VoiceRoomSeatEntity {
    public String userName;
    public boolean isTalk;
    public boolean isPlaceHolder;

    public VoiceRoomSeatEntity(boolean isPlaceHolder) {
        this.isPlaceHolder = isPlaceHolder;
    }
}
