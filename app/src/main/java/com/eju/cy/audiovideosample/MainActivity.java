package com.eju.cy.audiovideosample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.eju.cy.audiovideo.im.entrance.EjuImController;
import com.eju.cy.audiovideo.trtc.TRTCNewRoomActivity;
import com.eju.cy.audiovideo.utils.TypeState;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        CustomAVCallUIController.getInstance().setActivityContext(MainActivity.this);

        EjuImController.getInstance().setAppMainActivity(MainActivity.this);

        findViewById(R.id.tv_yingping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, CreateAudioCallActivity.class);
                // startActivity(intent);

//                CustomAVCallUIController.getInstance().createVideoCallRequest(MainActivity.this,
//                        getIntent().getStringExtra(Constants.LISTENING_USER_ID), ""
//                );


                EjuImController.getInstance().createAudioCallRequest(
                        "9027", "76961c79aadd50a89a4d5d452be1fdb9b75b53ed", TypeState.JDM,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584695135363&di=82976663fd154ebe2c36e545d3c5516c&imgtype=0&src=http%3A%2F%2Fpic35.photophoto.cn%2F20150519%2F0005018372430285_b.jpg", "张三", "211425", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584695135361&di=60631b32e147352ed52dc9287a44ed61&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F16%2F06%2F29%2F1057733240b0615.jpg", "李四", true
                );

            }
        });

        findViewById(R.id.tv_shiping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TRTCNewRoomActivity.class);
                intent.putExtra("TITLE", "视频通话");
                intent.putExtra("TYPE", "0");
                startActivity(intent);
            }
        });

    }
}
