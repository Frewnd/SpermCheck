package com.example.spermcheck;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;


import java.io.File;

import tools.GetPicture;

import static tools.RWResultAsTxt.saveVideoPath;

public class RecordVideoActivity extends AppCompatActivity {
    private static ProgressDialog dialog;
    public static void closeProgressDialog() {
        dialog.dismiss();
    }

    //连接两个activity时显示等待
    private int LOCAL_VIDEO = 0;

    private VideoView videoView;
    private Button confirmButton;
    private Intent intent = new Intent();
    private Intent intent2 = new Intent();
    private String[] result = new String[3];

    String[] msgIds = {"白色", "偏红", "偏黄"};
    int[] colorIds = {R.color.white, R.color.hh,  R.color.dh};
    String result_data = "";
    String videoPath = "";
    String videoName = "";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);

        final Spinner sp = this.findViewById(R.id.spinner);
        sp.setAdapter(ba);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //拿到被选择项的值
                result[0] = String.valueOf(msgIds[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        Intent intent = getIntent();
        videoName = intent.getStringExtra("VideoName") ;
        videoPath = Environment.getExternalStorageDirectory().getPath() + "/videos/"  + videoName + ".mp4";
//        Toast.makeText(RecordVideoActivity.this, videoPath, Toast.LENGTH_SHORT).show();

        videoView = findViewById(R.id.video_preview);
        videoView.setVideoPath(videoPath);
        videoView.setZOrderOnTop(true);//设置视频显示在最上层
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(videoView);
        mediaController.setVisibility(View.INVISIBLE); //设置隐藏控制栏
        videoView.requestFocus();
        videoView.start();
        String videoSavePath = getExternalFilesDir(videoName + "/Data") + File.separator + "videoPathData.txt";
        saveVideoPath(videoPath, videoSavePath);

        //确认按钮的实现
        confirmButton = findViewById(R.id.confirm_btu);
        confirmButton.setOnClickListener(v -> {
            EditText editText2 = findViewById(R.id.outsetVolume);
            EditText editText3 = findViewById(R.id.outsetTime);
            if (editText2.getText().toString().trim().equals("")) {
                new AlertDialog.Builder(RecordVideoActivity.this)
                        .setTitle("警告").setMessage("请输入精液量！")
                        .setPositiveButton("确定", null).show();
                return;

            } else if (editText3.getText().toString().trim().equals("")) {
                new AlertDialog.Builder(RecordVideoActivity.this)
                        .setTitle("警告").setMessage("请输入禁欲时间！")
                        .setPositiveButton("确定", null).show();
                return;

            } else {

                result[1] = editText2.getText().toString();
                result[2] = editText3.getText().toString();
                result_data = result[0] + "," + result[1] + "," + result[2];
                String result_path = getExternalFilesDir(videoName + "/Data") + File.separator + "Result.txt";
                saveVideoPath(result_data, result_path);


                Handler handler = new Handler();
                Runnable runnable = () -> {
                    final String pathPic = getExternalFilesDir(videoName + "/OriginalPicture") + File.separator ;  // 图像保存路径
                    GetPicture.getPicture(videoPath,pathPic);
                    Intent intent1 = new Intent();
                    intent1.setClass(RecordVideoActivity.this, PictureProcessActivity.class);
                    intent1.putExtra("FileName",videoName);
                    startActivity(intent1);
                };
                handler.post(runnable);
                dialog=ProgressDialog.show(RecordVideoActivity.this,"请稍等","正在分析，预计需要三分钟~",true,true);
            }
        });
    }

    //显示外观输入
    BaseAdapter ba = new BaseAdapter() {
        public int getCount() {
            return msgIds.length;
        }

        public Object getItem(int position) {
            return msgIds[position];
        }

        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        //动态生成每个下拉项对应的View，每个下拉项View由LinearLayout中包含一个ImageView及一个TextView构成
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ResourceType") View view = View.inflate(RecordVideoActivity.this, R.xml.input_item, null);
            TextView mTextView = (TextView) view.findViewById(R.id.tv_list);
            view.setBackgroundResource(colorIds[position]);
            mTextView.setText(msgIds[position]);
            return view;
        }
    };

}
