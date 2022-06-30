package tools;


import android.util.Log;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.GaussianBlur;

public class AnalysisMat {

    private static ArrayList<Double> KElements;

    public static int[] morphologyDemo(Mat src, Mat dst, int id) {

        Mat binary = new Mat();
        Mat hierarchy = new Mat();
        Mat tempMat = new Mat();
        int num = 0; // 细胞总数

        Core.multiply(tempMat,new Scalar(1.5,1.5,1.5),tempMat);
        GaussianBlur(dst, tempMat, new Size(3, 3), 0, 0);   // 高斯滤波
        Imgproc.threshold(tempMat, binary, 135,255, Imgproc.THRESH_BINARY); // 二值
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        int size = contours.size(); // 轮廓总数
        int [] point_position = new int[(size + 1) * 2];
        for(int i=0; i<size; i++) {


            double area = Imgproc.contourArea(contours.get(i), false); //面积
            Rect rect = Imgproc.boundingRect(contours.get(i)); // 创建外接矩形

            double w = rect.width;
            double h = rect.height;
            if (area < 10 || area/(w * h) < 0.49 || area > 60){
                continue;
            }
            double x = rect.x + w/2;
            double y = rect.y + h/2;
            num = num + 1;

            point_position[(i+1) * 2] = (int)x;
            point_position[(i+1) * 2 + 1] = (int)y;

            String text = String.valueOf(num);  // 标记数字
            Imgproc.putText(src, text, new Point(x, y), Imgproc.FONT_HERSHEY_SIMPLEX, 0.3, new Scalar(0, 0, 255));
            Imgproc.drawContours(src, contours, i, new Scalar(	173,255,47, 255), 1);
        }
        point_position[0] = id;
        point_position[1] = num;
        return point_position;
    }

    /*
     *  方法名：获取视野轮廓
     *  src 表示绘制轮廓的底板图片
     *  dst 表示获取轮廓数据的图片
     */
    public static void getBorder(Mat src, Mat dst){
        Mat tempMat = new Mat();
        Imgproc.threshold(dst, tempMat, 110, 255,Imgproc.THRESH_BINARY);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(tempMat, contours,  hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i = 0; i<contours.size(); i++) {
            Scalar color = new Scalar(	255,0,0, 255);
            Imgproc.drawContours(src, contours, i, color, 6);
        }
    }

    //图像相减
    public static int subtractMat(Mat first, Mat second, Mat k) {
        Mat dst = new Mat();
        Mat hierarchy = new Mat();
        Mat grayMat = new Mat();
        Imgproc.cvtColor(second,grayMat,Imgproc.COLOR_BGR2GRAY);

        Core.absdiff(first,grayMat,dst);
        Imgproc.threshold(dst,dst,50,255, Imgproc.THRESH_BINARY);
        Imgproc.morphologyEx(dst,dst, Imgproc.MORPH_DILATE,k);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Log.i("个数", contours.size() + "");
        return contours.size();
    }
}
