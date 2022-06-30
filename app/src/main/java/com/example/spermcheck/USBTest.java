package com.example.spermcheck;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.spermcheck.view.USBCameraActivity;

/**
 * @author: Lawrence
 * date:  7/16/2020--7:44 PM
 * tel:  18800273121
 * e-mail:  xbstyles@163.com
 * version:  1.0
 * introduce:  测试
 */
public class USBTest extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbcamera2);

        VideoView videoView = findViewById(R.id.usb_videoview);
        Uri mUri = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.dxq);
        videoView.setVideoURI(mUri);
        videoView.start();

        Button mVideoButton = findViewById(R.id.video_button);
        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(USBTest.this, RecordVideoActivity.class);
                String videoName = "123";
                intent.putExtra("VideoName",videoName);
                startActivity(intent);        
            }
        });
    }
}
