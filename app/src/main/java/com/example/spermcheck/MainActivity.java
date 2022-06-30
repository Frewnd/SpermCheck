package com.example.spermcheck;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import tools.CheckQQ;

import static tools.GetFileAllName.getFilesAllName;
import static tools.RWResultAsTxt.loadFromSDFile;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final int ALL_REQUEST_CODE = 88;


    private static ProgressDialog dialog;
    public static void closeProgressDialog() {
        dialog.dismiss();
    }

    String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };

    private TextView printDate;
    private ImageView testImage,historyImage,exampleImage,consultImage;


    private PieChartView pie_chart; //饼形图控件
    private PieChartData pieChartData;   //数据
    List<SliceValue> values = new ArrayList<SliceValue>();
    private int[] colorData = {Color.parseColor("#ec063d"),
            Color.parseColor("#f1c704"),
            Color.parseColor("#c9c9c9"),
            Color.parseColor("#2bc208"),
            Color.parseColor("#333333")};
    private String[] stateChar = {"体积","颜色", "总数", "浓度", "活性"};
    private double total_score = 0;
    private String fileName = "default";

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> fileNames = getFilesAllName(getExternalFilesDir("") ); // 获取主文件下有多少个子文件，代表检测了多少次

        for(String file : fileNames){
            Log.i("文件名",file+ "");
        }

        getPermission();
        staticLoadCVLibraries();    //初始化opencv


        /**
          * 初始化饼图，判断之前是否又检测数据；
          * 若有，则引用数据；
          * 若无，则默认数据；
         */
        double score[] = new double[stateChar.length + 1];
        String advice[] = new String[stateChar.length + 1];
        try {
            fileName = fileNames.get(0);
            String score_path = getExternalFilesDir(fileNames.get(0) + "/Data") + File.separator + "Score.txt";
            String advice_path = getExternalFilesDir(fileNames.get(0) + "/Data") + File.separator + "Advice.txt";

            String score_data = loadFromSDFile(score_path);
            String [] score_temp = score_data.split(",");

            String advice_data = loadFromSDFile(advice_path);
            String [] advice_temp = advice_data.split("-");

            for(int i=0; i < score_temp.length-1; i++){
                Log.i("文件名",score_temp[i] + "");
                score[i] = Double.parseDouble(score_temp[i+1]);
                advice[i] = advice_temp[i];
            }
            total_score = Double.valueOf(score_temp[0]);
        }catch (Exception e){
            score = new double[]{10, 10, 20, 30, 30, 100};
            total_score = score[5];
        }


        printDate = findViewById(R.id.print_date);
        testImage = findViewById(R.id.item_test);
        historyImage = findViewById(R.id.item_history);
        exampleImage = findViewById(R.id.item_example);
        consultImage = findViewById(R.id.item_consult);
        pie_chart = (PieChartView) findViewById(R.id.pie_chart);
        pie_chart.setOnValueTouchListener(selectListener);//设置点击事件监听

        setPieChartData(score);
        initPieChart();

        testImage.setOnClickListener(this);
        historyImage.setOnClickListener(this);
        exampleImage.setOnClickListener(this);
        consultImage.setOnClickListener(this);
        printDate.setOnClickListener(this);

    }

    private void setPieChartData(double [] score) {

        for (int i = 0; i < colorData.length; ++i) {
            SliceValue sliceValue = new SliceValue((float) score[i], colorData[i]);
            values.add(sliceValue);
        }
    }

    public void initPieChart() {
        pieChartData = new PieChartData();
        pieChartData.setHasLabels(true);//显示表情
        pieChartData.setHasLabelsOnlyForSelected(false);//不用点击显示占的百分比
        pieChartData.setHasLabelsOutside(false);//占的百分比是否显示在饼图外面
        pieChartData.setHasCenterCircle(true);//是否是环形显示
        pieChartData.setValues(values);//填充数据
        pieChartData.setCenterText1(total_score+"分");
        pieChartData.setCenterCircleColor(Color.WHITE);//设置环形中间的颜色
        pieChartData.setCenterCircleScale(0.7f);//设置环形的大小级别
        pie_chart.setPieChartData(pieChartData);
        pie_chart.setValueSelectionEnabled(true);//选择饼图某一块变大
        pie_chart.setAlpha(0.9f);//设置透明度
        pie_chart.setCircleFillRatio(1f);//设置饼图大小

    }
    /**
     * 监听事件
     */
    private PieChartOnValueSelectListener selectListener = new PieChartOnValueSelectListener() {

        @Override
        // 默认情况下，在中间部分显示相应信息
        public void onValueDeselected() {
            pieChartData.setCenterText1(total_score+"分");
        }

        @Override
        //选择对应图形后，在中间部分显示相应信息
        public void onValueSelected(int arg0, SliceValue value) {
            pieChartData.setCenterText1(stateChar[arg0]);
            pieChartData.setCenterText1Color(colorData[arg0]);
        }
    };

    //各类功能的实现
    @Override
    public void onClick(View v) {
        Handler handler = new Handler();
        Runnable runnable = () -> {
            Intent intent2 = new Intent();
            intent2.setClass(MainActivity .this,ResultShowActivity .class);
            intent2.putExtra("FileName",fileName);
            startActivity(intent2);
        };
        switch (v.getId()){
            //开始测试
            case R.id.item_test:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, BeginTestActivity.class);
                startActivity(intent);
                break;

            // 查看历史
            case R.id.item_history:
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this, HistoryResultActivity.class);
                startActivity(intent1);
                break;

            // 查看教程
            case R.id.item_example:
                Toast.makeText(MainActivity.this,"暂无例程！",Toast.LENGTH_SHORT ).show();
                break;

             //咨询医生
            case R.id.item_consult:
                CheckQQ checkQQ = new CheckQQ();
                if (checkQQ.isQQClientAvailable(this) != false){
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + 318603563;//uin是发送过去的qq号码
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }else{
                    Toast.makeText(MainActivity.this,"您还未安装QQ或TIM！",Toast.LENGTH_SHORT ).show();
                }
                break;

            case R.id.print_date:
                handler.post(runnable);
                dialog=ProgressDialog.show(MainActivity.this,"请稍等","正在加载，预计需要3秒钟~",true,true);
                break;
        }
    }

    public void getPermission(){
        //检测版本
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }
    }
    private void requestPermission() {
        // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
        List<String> mPermissionList = new ArrayList<>();
        mPermissionList.clear();
        for (String permission : REQUIRED_PERMISSION_LIST) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }
        if (mPermissionList.isEmpty()) {

        } else {
            ActivityCompat.requestPermissions(this,
                    mPermissionList.toArray(new String[mPermissionList.size()]),
                    ALL_REQUEST_CODE);
        }
    }

    //OpenCV库静态加载并初始化
    private void staticLoadCVLibraries(){
        boolean load = OpenCVLoader.initDebug();
    }
}