package com.eju.cy.audiovideo.trtc.customcapture;

import android.content.Context;
import android.media.MediaFormat;
import android.os.SystemClock;
import android.util.Log;


import com.eju.cy.audiovideo.trtc.customcapture.decoder.Decoder;
import com.eju.cy.audiovideo.trtc.customcapture.exceptions.ProcessException;
import com.eju.cy.audiovideo.trtc.customcapture.exceptions.SetupException;
import com.eju.cy.audiovideo.trtc.customcapture.extractor.Extractor;
import com.eju.cy.audiovideo.trtc.customcapture.extractor.ExtractorAdvancer;
import com.eju.cy.audiovideo.trtc.customcapture.extractor.RangeExtractorAdvancer;
import com.eju.cy.audiovideo.trtc.customcapture.structs.Frame;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef.TRTCAudioFrame;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AudioFrameReader extends BaseReader {
    private static final String TAG = "AudioFrameReader";
    private static final int FRAME_DURATION = 20;

    private final TRTCCloud mTRTCCloud;
    private final String mVideoPath;
    private final long mLoopDurationMs;

    private Decoder mAudioDecoder;
    private long mStartTimeMs = -1;
    private int mSampleRate;
    private int mChannels;
    private int mBytesPreMills;

    private byte[] mFrameData;
    private int mSize;
    private long mLastEndTime = 0;
    private volatile boolean mIsSendEnabled = true;

    public AudioFrameReader(Context context, String videoPath, long durationMs, CountDownLatch countDownLatch) {
        super(countDownLatch);
        mTRTCCloud = TRTCCloud.sharedInstance(context);
        mVideoPath = videoPath;
        mLoopDurationMs = durationMs;
    }

    public void enableSend(boolean enable) {
        mIsSendEnabled = enable;
    }

    @Override
    protected void setup() throws SetupException {
        ExtractorAdvancer advancer = new RangeExtractorAdvancer(MILLISECONDS.toMicros(mLoopDurationMs));
        Extractor extractor = new Extractor(false, mVideoPath, advancer);
        mAudioDecoder = new Decoder(extractor);
        mAudioDecoder.setLooping(true);
        mAudioDecoder.setup();

        MediaFormat mediaFormat = extractor.getMediaFormat();
        mSampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        mChannels = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        mBytesPreMills = mChannels * 2 * mSampleRate / 1000;

        // 每次只发送FRAME_DURATION长度的数据
        mFrameData = new byte[FRAME_DURATION * mBytesPreMills];
        mSize = 0;
    }

    @Override
    protected void processFrame() throws ProcessException {
        if (mStartTimeMs == -1) {
            mStartTimeMs = SystemClock.elapsedRealtime();
        }

        mAudioDecoder.processFrame();
        Frame frame = mAudioDecoder.dequeueOutputBuffer();
        if (frame == null) {
            return;
        }

        // 检查时间戳是否连上，如果没连上，则中间插点静音数据
        long diff = MICROSECONDS.toMillis(frame.presentationTimeUs) - (mLastEndTime + mSize / mBytesPreMills);
        while (diff > 0) {
            Log.v(TAG, "diff: " + diff);
            int zeroCount = (int) Math.min(diff * mBytesPreMills, mFrameData.length - mSize);
            Arrays.fill(mFrameData, mSize, mSize + zeroCount, (byte) 0);
            mSize += zeroCount;
            diff -= zeroCount / mBytesPreMills;
            waitAndSend();
        }

        while (frame.size > 0) {
            int readSize = Math.min(mFrameData.length - mSize, frame.size);
            frame.buffer.position(frame.offset);
            frame.buffer.get(mFrameData, mSize, readSize);
            frame.size -= readSize;
            frame.offset += readSize;
            mSize += readSize;
            waitAndSend();
        }

        mAudioDecoder.enqueueOutputBuffer(frame);
    }

    private void waitAndSend() {
        if (mSize < mFrameData.length) {
            return;
        }

        // 检查当前帧与预期发送的时间差多久，睡眠这段时间，然后再发送
        long time = SystemClock.elapsedRealtime() - mStartTimeMs;
        if (mLastEndTime > time) {
            try {
                Thread.sleep(mLastEndTime - time);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        TRTCAudioFrame trtcAudioFrame = new TRTCAudioFrame();
        trtcAudioFrame.data = mFrameData;
        trtcAudioFrame.sampleRate = mSampleRate;
        trtcAudioFrame.channel = mChannels;
        // 模拟不带时间戳
        // trtcAudioFrame.timestamp = mLastEndTime;

        if (mIsSendEnabled) {
            mTRTCCloud.sendCustomAudioData(trtcAudioFrame);
        }

        // 每次发送的时间长度为FRAME_DURATION
        mLastEndTime += FRAME_DURATION;
        mSize = 0;
    }

    @Override
    protected void release() {
        if (mAudioDecoder != null) {
            mAudioDecoder.release();
            mAudioDecoder = null;
        }
    }
}
