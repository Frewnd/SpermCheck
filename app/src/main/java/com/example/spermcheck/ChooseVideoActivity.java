package com.example.spermcheck;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
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

import java.io.File;

import tools.GetPicture;

import static tools.RWResultAsTxt.saveVideoPath;

public class ChooseVideoActivity extends AppCompatActivity {

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
    String videoName = "";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);

        final Spinner sp = (Spinner) this.findViewById(R.id.spinner);
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

        //本地获取
        intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent2.setType("video/*");    // 这里是设置打开文件的类型为MP4
        intent.addCategory("android.intent.category.DEFAULT");
        startActivityForResult(intent2, LOCAL_VIDEO);//定义处理浏览结果的类，也就是说点击某一个文件后需要执行的操作，VIDEO_CAPTURE是参数，在多个按钮时使用

        videoView = findViewById(R.id.video_preview);
        videoView.setOnTouchListener((v,event) ->{
            if (videoView.isPlaying()) {
                videoView.pause();
            } else {
                videoView.start();
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == LOCAL_VIDEO) {
            final Uri videoUri = data.getData();
            final String path1 = getAbsoluteImagePath(videoUri);// 将uri转为真实地址

            File tempFile = new File(path1); // 创建临时文件
            videoName = tempFile.getName(); // 获取选择的视频文件名称，用作与创建相应的文件夹

            videoView.setVideoURI(videoUri);
            videoView.setZOrderOnTop(true);//设置视频显示在最上层
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            mediaController.setMediaPlayer(videoView);
            mediaController.setVisibility(View.INVISIBLE); //设置隐藏控制栏
            videoView.requestFocus();
            videoView.start();
            String videoPath = getExternalFilesDir(videoName + "/Data") + File.separator + "videoPathData.txt";
            saveVideoPath(path1, videoPath);

            //确认按钮的实现
            confirmButton = findViewById(R.id.confirm_btu);
            confirmButton.setOnClickListener(v -> {
                EditText editText2 = findViewById(R.id.outsetVolume);
                EditText editText3 = findViewById(R.id.outsetTime);
                if (editText2.getText().toString().trim().equals("")) {
                    new AlertDialog.Builder(ChooseVideoActivity.this)
                            .setTitle("警告").setMessage("请输入精液量！")
                            .setPositiveButton("确定", null).show();
                    return;

                } else if (editText3.getText().toString().trim().equals("")) {
                    new AlertDialog.Builder(ChooseVideoActivity.this)
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
                        GetPicture.getPicture(path1,pathPic);
                        Intent intent = new Intent();
                        intent.setClass(ChooseVideoActivity.this, PictureProcessActivity.class);
                        intent.putExtra("FileName",videoName);
                        startActivity(intent);
                    };
                    handler.post(runnable);
                    dialog=ProgressDialog.show(ChooseVideoActivity.this,"请稍等","正在分析，预计需要三分钟~",true,true);
                }
            });
        }
    }

    //获取视频文件真实路径
    protected String getAbsoluteImagePath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
            @SuppressLint("ResourceType") View view = View.inflate(ChooseVideoActivity.this, R.xml.input_item, null);
            TextView mTextView =  view.findViewById(R.id.tv_list);
            view.setBackgroundResource(colorIds[position]);
            mTextView.setText(msgIds[position]);
            return view;
        }
    };
}
