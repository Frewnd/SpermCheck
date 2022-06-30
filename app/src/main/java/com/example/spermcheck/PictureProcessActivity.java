package com.example.spermcheck;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;


import static tools.AnalysisMat.morphologyDemo;
import static tools.AnalysisMat.subtractMat;
import static tools.ArrangeArray.averageArr;

import static tools.ArrangeArray.deleteZero;
import static tools.RWResultAsTxt.loadFromSDFile;
import static tools.RWResultAsTxt.readTxt;
import static tools.RWResultAsTxt.savePosition;
import static tools.RWResultAsTxt.saveVideoPath;


public class PictureProcessActivity extends AppCompatActivity {
    private static ProgressDialog dialog;
    public static void closeProgressDialog() {
        dialog.dismiss();
    }

    private static final int EVENT_TIME_TO_CHANGE_IMAGE = 100;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            Message message = mHandler.obtainMessage(EVENT_TIME_TO_CHANGE_IMAGE);
            mHandler.sendMessage(message);
        }

    }
    private final Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_TIME_TO_CHANGE_IMAGE:
                    ChooseVideoActivity.closeProgressDialog();
                    break;
            }
        }
    };

    Handler handler = new Handler();
    String result_data = "";
    String fileName = "";

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("FileName");


        File file = getExternalFilesDir(fileName + "/OriginalPicture");
        final File[] files = file.listFiles();//获取照片数量
        int size = files.length;


        int total_sperm []= new int[size];
        int vigour_sperm [] = new int[size - 1];
        //固定数组长度，会浪费内存，但是可以保证数组不会溢出，后续可以使用自定义的动态数组；
        int pre_point[] = new int[2000];
        int pre_sum[] = new int[1000];
        double pre_distance[] = new double[1000];
        double sum_distance[] = new double[1000];

        Mat srcMat = new Mat();
        Mat gayMat = new Mat();
        Mat tempMat = new Mat();

        // 根据second图片,创建一个空白图层用于绘制轨迹
        @SuppressLint("ResourceType")
        InputStream second = getResources().openRawResource(R.drawable.second);
        Bitmap drawBit = BitmapFactory.decodeStream(second);
        Utils.bitmapToMat(drawBit,tempMat);
        Mat drawMat = new Mat(tempMat.rows(),tempMat.cols(),tempMat.type());

        Mat s = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(10, 10), new Point(-1, -1));// 创建结构元素
        String point_path = getExternalFilesDir(fileName + "/Data") + File.separator  + "Point.txt";
        String point_sum_path = getExternalFilesDir(fileName + "/Data") + File.separator + "PointSum.txt";
        String distance_sum_path = getExternalFilesDir(fileName + "/Data") + File.separator + "DistanceSum.txt";

        Runnable runnable = new Runnable() {
            FileOutputStream fos1;
            Mat preMat = new Mat();
            @Override
            public void run() {
                //遍历原始视频帧
                for (int i = 1; i <= size; i++){
                    //原始视频帧路径
                    String pathPic = getExternalFilesDir(fileName + "/OriginalPicture") + File.separator  + i + ".jpg";
                    Bitmap bitmap = BitmapFactory.decodeFile(pathPic);

                    try{
                        //图片灰度化
                        Utils.bitmapToMat(bitmap,srcMat);
                        Imgproc.cvtColor(srcMat,gayMat,Imgproc.COLOR_BGR2GRAY);

                        /**
                         * 寻找精子目标，返回坐标
                         * picture_result[0]为图片的帧数；
                         * picture_result[1]为本帧中目标个数；
                         * 其余为检测目标的横纵坐标；
                         */
                        int [] picture_result = morphologyDemo(srcMat,gayMat,i);

                        /**
                         * 保存每张图片目标精子质心坐标的位置，存为txt文件
                         * point_position为去除原点后的目标横纵坐标(x,y)；
                         * point_sum为目标横纵坐标合(x+y)；
                         */
                        String point_position = "";
                        String point_sum = "";
                        int point_position_sum = 0;
                        for(int j = 1; j < picture_result.length/2; j++) {
                            point_position_sum = picture_result[j*2] + picture_result[j*2+1];
                            if (j == picture_result.length/2-1) {
                                // 坐标去除（0，0）坐标
                                if (point_position_sum==0){
                                    point_position = point_position + "\n";
                                    point_sum = point_sum + "\n";
                                    continue;
                                }else {
                                    point_position = point_position + picture_result[j * 2] + "," + picture_result[j * 2 + 1] + "-1\n"; // 以-1结尾
                                    point_sum = point_sum + point_position_sum + "-1\n";
                                }
                                break;
                            }else {
                                if (point_position_sum==0){
                                    continue;
                                }else {
                                    point_position = point_position + picture_result[j * 2] + "," + picture_result[j * 2 + 1] + ",";
                                    point_sum = point_sum + point_position_sum + ",";
                                }
                            }
                        }
                        savePosition(point_position, point_path);
                        savePosition(point_sum, point_sum_path);
                        total_sperm[i-1] = picture_result[1]; //每帧图片的总精子数为picture_result[1]

                        if (i != 1) {
                            vigour_sperm[i-2] = subtractMat(preMat,srcMat,s);    // 活得精子数
//                            //处理后保存的路径
//                            String pathPic1 = getExternalFilesDir(fileName + "/ManagePicture") + File.separator + File.separator + (i-1) + ".jpg";
//                            fos1 = new FileOutputStream(pathPic1);
//                            Utils.matToBitmap(srcMat,bitmap);
//                            bitmap.compress(Bitmap.CompressFormat.JPEG,70,fos1);
//                            fos1.close();
                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                    preMat = gayMat;
                }


                // 目标跟踪并绘制轨迹
                for (int i = 1; i <= size - 1; i++) {
                    //处理后的帧图片路径
                    String original_path = getExternalFilesDir(fileName + "/OriginalPicture") + File.separator + i + ".jpg";
                    String manage_path = getExternalFilesDir(fileName + "/ManagePicture") + File.separator + i + ".jpg";
                    Bitmap bitmap = BitmapFactory.decodeFile(original_path);
                    Utils.bitmapToMat(bitmap, srcMat);

                    try {
                        List position_list = readTxt(point_path);
                        List point_sum_list = readTxt(point_sum_path);
                        String point_position = (String) position_list.get(i - 1);
                        String point_sum = (String) point_sum_list.get(i - 1);
                        String point[] = point_position.split(",");
                        String sum[] = point_sum.split(",");

                    /**
                     * 根据本帧坐标和sum，在前一帧坐标和pre_sum中寻找对应目标
                     * 如果在一定范围内寻到到对应目标，则记录对应目标在pre_sum中的下标，同时将对应的pre_sum元素改为-1，表示已经有匹配对象；
                     *
                     */
                    int[] index = new int[point.length]; // 保存寻找到对应目标的数组下标
                    double distance[] = new double[sum.length];
                    if(i==1){
                        Log.i("Tracking","这是第一帧！");
                    }else {
                        for(int j=0; j<sum.length; j++){
                            for(int k=0; k<pre_sum.length; k++){
                                //坐标和相等，表示该精子未移动，不用连线
                                int x_temp = Integer.valueOf(point[j*2])-pre_point[k*2]; // 帧间x的变化量
                                int y_temp = Integer.valueOf(point[j*2+1])-pre_point[k*2+1]; // 帧间y的变化量
                                if(Integer.valueOf(sum[j])==pre_sum[k]){
                                    index[j] = k;
                                    distance[j] = 0;
                                    pre_sum[k] = -1;
                                //表示坐标和的差值的绝对值小于等于5，同时相应的横纵坐标的差值绝对值小于等于8，匹配到对应目标，则画出连线；
                                }else if (Math.abs(Integer.valueOf(sum[j]) - pre_sum[k])<=5 && Math.abs(x_temp)<=8 && Math.abs(y_temp)<=8){
                                    index[j] = k;
                                    distance[j] =Math.sqrt(Math.pow(x_temp,2) + Math.pow(y_temp,2)); // 计算对应目标点之间的运动距离
                                    Imgproc.line(drawMat, new Point(Integer.valueOf(point[j*2]),Integer.valueOf(point[j*2+1])), new Point(pre_point[k*2],pre_point[k*2+1]), new Scalar(255,0,0,255),2);
                                    Core.add(srcMat,drawMat,srcMat);
                                    pre_sum[k] = -1;
                                }else{
                                    // 如果没有在上一帧匹配到对应目标（可能上一帧目标消失或本帧新出现）
                                    distance[j] = 0;
                                    index[j] = -1;
                                }
                                sum_distance[k] = sum_distance[k] + distance[j]; // 计算对应精子的总路程
                            }
                        }
                    }
                    // 更新上一帧的坐标
                    Log.i("point.length",point.length+"");
                    for(int j=0; j<point.length; j++){
                        pre_point[j] = Integer.valueOf(point[j]);
                        if (j==point.length-1 && pre_point[j+1]!=0){
                            for(int k=j; k<pre_point.length; k++){
                                pre_point[k] = 0;
                            }
                        }
                    }
                        // 更新上一帧的坐标和
                        for(int j=0; j<sum.length; j++){
                            pre_sum[j] = Integer.valueOf(sum[j]);
                            if (j==sum.length-1 && pre_sum[j+1]!=0){
                                for(int k=j; k<pre_sum.length; k++){
                                    pre_sum[k] = 0;
                                }
                            }
                        }
                        // 更新上一帧的运动距离
                        for(int j=0; j<distance.length; j++){
                            pre_distance[j] = distance[j];
                            if (j==distance.length-1 && pre_distance[j+1]!=0){
                                for(int k=j; k<pre_distance.length; k++){
                                    pre_distance[k] = 0;
                                }
                            }
                        }
                        try {
                            fos1 = new FileOutputStream(manage_path);
                            Utils.matToBitmap(srcMat,bitmap);
                            bitmap.compress(Bitmap.CompressFormat.JPEG,90,fos1);//储存
                            fos1.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {

                    }

                }
                /**
                 * 保存所有精子目标在整个视频中的总运动路程
                 */
                String distance = "";
                double [] new_distance = deleteZero(sum_distance);
                for(int i=0;i<new_distance.length;i++){
                    if(i == new_distance.length-1){
                        distance = distance + String.format("%.2f",new_distance[i]/(size-1));
                    }else {
                        distance = distance + String.format("%.2f",new_distance[i]/(size-1)) + ",";
                    }
                }
                savePosition(distance, distance_sum_path);

                String result_path = getExternalFilesDir(fileName + "/Data") + File.separator + "Result.txt";
                result_data = loadFromSDFile(result_path);
                int realAllNum = averageArr(total_sperm);
                try {
                    result_data = result_data + "," + (float) ((averageArr(vigour_sperm) * 100) / realAllNum);
                }catch (Exception e){
                    result_data = result_data + "," + 0;
                }
                result_data  = result_data + "," + realAllNum;
                saveVideoPath(result_data, result_path);

                Intent intent = new Intent();
                intent.setClass(PictureProcessActivity.this, ResultShowActivity.class);
                intent.putExtra("FileName",fileName);
                startActivity(intent);
            }
        };
        handler.post(runnable);
        dialog= ProgressDialog.show(PictureProcessActivity.this,"请稍等","正在排版结果，预计需要30秒~",true,true);
    }
}