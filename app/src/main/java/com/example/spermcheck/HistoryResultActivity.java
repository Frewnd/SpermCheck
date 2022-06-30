package com.example.spermcheck;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bean.ScoreRateBean;
import lecho.lib.hellocharts.view.LineChartView;
import tools.ScoreTool;

import static tools.GetFileAllName.getFilesAllName;
import static tools.JudgeTotalResult.analysis;
import static tools.RWResultAsTxt.loadFromSDFile;
import static tools.ShowList.setListViewHeightBasedOnChildren;

public class HistoryResultActivity extends AppCompatActivity {

    private LineChartView lineChart;
    private TextView textView;
    private List<ScoreRateBean> heartList = new ArrayList<>();
    private  String [] names= new String[5];


    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lineChart = (LineChartView) this.findViewById(R.id.chart);
        textView = (TextView)findViewById(R.id.history_advice);


        List<String> fileNames = getFilesAllName(getExternalFilesDir("") ); // 获取主文件下有多少个子文件，代表检测了多少次
        String date[] ={"示例","示例","示例","示例","示例"};
        double score[] ={0,0,0,0,0};
        try {
            for (int i = 0; i <fileNames.size(); i++) {
                if (fileNames.get(i).equals("default")){ // 如果文件名等于默认，则跳过
                    fileNames.remove(i);
                    i--;
                    continue;
                }
                String score_path = getExternalFilesDir(fileNames.get(i) + "/Data") + File.separator + "Score.txt";
                String advice_path = getExternalFilesDir(fileNames.get(i) + "/Data") + File.separator + "Advice.txt";

                String score_data = loadFromSDFile(score_path);
                String[] score_temp = score_data.split(",");

                String advice_data = loadFromSDFile(advice_path);
                String[] advice_temp = advice_data.split("-");

                date[i] = advice_temp[0];
                score[i] = Double.parseDouble(score_temp[0]);
                names[i] = advice_temp[0] +"日        " + score_temp[0] + "分             ";
                heartList.add(new ScoreRateBean((float) score[i], date[i] + ""));
            }
            textView.setText("从最近的检测数据来看，你的精子质量较为健康！");
            ScoreTool.setChartViewData(heartList, lineChart);
        } catch (Exception e) {
            for (int i=0; i<date.length; i++){
                heartList.add(new ScoreRateBean((float) score[i], date[i] + ""));
            }

            ScoreTool.setChartViewData(heartList, lineChart);
        }

        try{
            //显示结果列表
            ListView mListView = findViewById(R.id.history_result);
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

                @SuppressLint({"ResourceAsColor", "ResourceType"})
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    @SuppressLint("ResourceType") View view = View.inflate(HistoryResultActivity.this, R.xml.history_list_item, null);
                    TextView mTextView = (TextView) view.findViewById(R.id.tv_list);
                    //判断分数，大于60分绿色显示，小于60红色显示；
                    if(score[position]>60){
                        mTextView.setTextColor( R.color.green);
                    }else {
                        mTextView.setTextColor( R.color.red);
                    }

                    //组件一拿到，开始组装
                    mTextView.setText(names[position]);
                    return view;
                }
            };
            mListView.setAdapter(ba);
            setListViewHeightBasedOnChildren(mListView);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        String fileName = fileNames.get(i);
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Intent intent2 = new Intent();
                                intent2.setClass(HistoryResultActivity .this,ResultShowActivity .class);
                                intent2.putExtra("FileName",fileName);
                                startActivity(intent2);
                            }
                        };
                        handler.post(runnable);
                    }catch (Exception e){
                        Intent intent2 = new Intent();
                        intent2.setClass(HistoryResultActivity .this,ResultShowActivity .class);
                        intent2.putExtra("FileName","");
                        startActivity(intent2);
                    }
                }
            });
        }catch (Exception e){

        }
    }
}
