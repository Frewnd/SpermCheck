package com.example.spermcheck;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.spermcheck.view.USBCameraActivity;

import java.io.File;


public class BeginTestActivity extends AppCompatActivity {
    private Button beginButton;
    private ListView mListView;
    private  String[] names={"样本采集并处理","录制精子运动视频","视频处理与分析","获取结果并生成报告"};
    private int[] icons={R.drawable.prepare_caiji,R.drawable.prepare_jiagong,R.drawable.prepare_fenxi,R.drawable.prepare_jieguo};

    private static final int TAKE_VIDEO = 0;
    private Intent intent = new Intent();
    File videoFile = new File(Environment.getExternalStorageDirectory().getPath()+"/test_video.mp4");
    Uri videoUri = FileProvider.getUriForFile(BeginTestActivity.this, "com.example.login", videoFile);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mListView = findViewById(R.id.lv);
        mListView.setAdapter(new MyBaseAdapter());


        //开始测试按钮的实现
        beginButton = (Button) findViewById(R.id.begin_btu);
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BeginTestActivity.this)
                    .setTitle("从哪里获得视频")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(new String[]{"现场录制", "本地获取"}, 0,
                            (dialog, which) -> {
                               if (which == 0){
                                   Intent intent3 = new Intent();
                                   intent3.setClass(BeginTestActivity.this, USBCameraActivity.class);
                                   startActivity(intent3);
                                   dialog.cancel();
                               }else if (which == 1){
                                   Intent intent2 = new Intent();
                                   intent2.setClass(BeginTestActivity.this, ChooseVideoActivity.class);
                                   startActivity(intent2);
                                   dialog.cancel();
                               }
                            })
                    .setNegativeButton("取消", null)
                    .show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_VIDEO) {
                Intent intent3 = new Intent();
                intent3.setClass(BeginTestActivity.this, RecordVideoActivity.class);
                startActivity(intent3);
            }
        }
    }

    class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return names [position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {//组装数据
            @SuppressLint("ResourceType") View view=View.inflate(BeginTestActivity.this,R.xml.list_item,null);//在list_item中有两个id,现在要把他们拿过来
            TextView mTextView=(TextView) view.findViewById(R.id.tv_list);
            ImageView imageView=(ImageView)view.findViewById(R.id.image);
            //组件一拿到，开始组装
            mTextView.setText(names[position]);
            imageView.setBackgroundResource(icons[position]);
            //组装玩开始返回
            return view;
        }
    }
}