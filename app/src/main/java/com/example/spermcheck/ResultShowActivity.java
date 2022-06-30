package com.example.spermcheck;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import static android.graphics.Color.*;
import static tools.GetTime.getNowId;
import static tools.GetTime.getNowTime;
import static tools.JudgeResult.judge;
import static tools.RWResultAsTxt.loadFromSDFile;
import static tools.ShowList.setListViewHeightBasedOnChildren;

/**
 * @author： Lawrence
 * date：  6/25/2020 4:04 PM
 * tel：  18800273121
 * e-mail：  xbstyles@163.com
 * version：  1.0
 */
public  class ResultShowActivity extends AppCompatActivity {

    private String fileName = "";

    private ImageSwitcher mImageSwitcher;
    private ListView mListView;
    private  String[] names={"外观颜色：","精液量：","禁欲时间:","存活率：","精子浓度：","前向运动（PR）：","非前向运动（NP)：","不动(IM）："};
    private int colorIds[] = new int [names.length];
    private  String[] result = new String[names.length + 1];
    private  String[] analyse = new String[names.length + 1];


    @SuppressLint("ResourceAsColor")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_show);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("FileName");

        TextView resultTextView = findViewById(R.id.tex);
        TextView timeTextView = findViewById(R.id.tv_time);
        TextView idTextView = findViewById(R.id.tv_id);
        TextView adviceTextView = findViewById(R.id.tex_advice);
        TextView scoreTextView = findViewById(R.id.tv_score);

        try {
            String result_path = getExternalFilesDir(fileName + "/Data") + File.separator + "Result.txt";
            String score_path = getExternalFilesDir(fileName + "/Data") + File.separator + "Score.txt";
            String advice_path = getExternalFilesDir(fileName + "/Data") + File.separator + "Advice.txt";
            String distance_sum_path = getExternalFilesDir(fileName + "/Data") + File.separator + "DistanceSum.txt";

            String result_data = loadFromSDFile(result_path);
            String[] temp = result_data.split(",");
            String distance_data = loadFromSDFile(distance_sum_path);
            String[] distance_temp = distance_data.split(",");
            for(int i=0; i<distance_temp.length; i++){
                Log.i("distance",distance_temp[i]);
            }

            for (int i = 0; i < temp.length; i++) {
                result[i] = temp[i];
            }
            colorIds = judge(result, analyse, score_path, advice_path,distance_temp);
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i] + result[i];
            }
        } catch (Exception e) {
            analyse[5] = "您本次检测结果异常。";
            analyse[6] = "请咨询医生或到医院进一步检测！";
            analyse[7] = "0分";
        }

        resultTextView.setText(analyse[5]);
        resultTextView.setOnClickListener(view -> {
            Intent intent1 = new Intent();
            intent1.setClass(ResultShowActivity.this, MainActivity.class);
            startActivity(intent1);
        });

        idTextView.setText(getNowId());
        timeTextView.setText(getNowTime());
        adviceTextView.setText(analyse[6]);
        scoreTextView.setText(analyse[7]);

        // 判断检测结果是否异常，正常为默认的绿色，异常为红色
        try {
            if (analyse[5].length() > 10) {
                resultTextView.setTextColor(RED);
            } else {
//                resultTextView.setTextColor(GREEN);
            }
        }catch (Exception e){
            resultTextView.setTextColor(RED);
        }
        try{
             //目标跟踪显示
            File manageFile = getExternalFilesDir(fileName + "/ManagePicture");
            final File[] manageFiles = manageFile.listFiles();//获取照片数量
            int manageFileSize = manageFiles.length;
            if (manageFileSize != 0) {
                final Drawable[] drawable = new Drawable[manageFileSize];
                for (int i = 1; i <= manageFileSize; i++) {
                    String pathPic = getExternalFilesDir(fileName + "/ManagePicture") + File.separator + File.separator + i + ".jpg";
                    Bitmap bitmap = BitmapFactory.decodeFile(pathPic);
                    drawable[i - 1] = new BitmapDrawable(bitmap);
                }

                mImageSwitcher = findViewById(R.id.imageSwitcher);
                mImageSwitcher.setFactory(() -> {
                    // makeView返回的是当前需要显示的ImageView控件，用于填充进ImageSwitcher中
                    return new ImageView(ResultShowActivity.this);
                });
                mImageSwitcher.postDelayed(new Runnable() {
                    int currentIndex = 0;

                    @Override
                    public void run() {
                        mImageSwitcher.setBackgroundDrawable(drawable[currentIndex]);
                        if (currentIndex == (manageFileSize - 1))
                            currentIndex = 0;
                        else
                            currentIndex++;
                        mImageSwitcher.postDelayed(this, 40);
                    }
                }, 40);
            }
        }catch (Exception e){

        }

        try{
            //显示结果列表
            mListView= findViewById(R.id.lv_result);
            BaseAdapter ba = new BaseAdapter() {
                public int getCount() {
                    return names.length;
                }

                public Object getItem(int position) {
                    return names[position];
                }

                public long getItemId(int position) {
                    return position;
                }

                @SuppressLint("ResourceAsColor")
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    @SuppressLint("ResourceType") View view = View.inflate(ResultShowActivity.this, R.xml.result_list, null);
                    TextView mTextView = view.findViewById(R.id.result_text);
                    view.setBackgroundResource(colorIds[position]);
                    //组件一拿到，开始组装
                    mTextView.setText(names[position]);
                    return view;
                }
            };
            mListView.setAdapter(ba);
            setListViewHeightBasedOnChildren(mListView);
        }catch (Exception e){

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Message message = mHandler.obtainMessage(0);
            mHandler.sendMessage(message);
        }

    }
    private final Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        MainActivity.closeProgressDialog();
                        PictureProcessActivity.closeProgressDialog();
                    }catch (Exception e){

                    }
                    break;
            }
        }
    };
}