package com.eju.cy.audiovideo.trtc.customcapture.structs;

import android.opengl.EGLContext;

public class TextureFrame {
    public EGLContext eglContext;

    public int textureId;

    public int width;

    public int height;

    public long timestampMs;
}
