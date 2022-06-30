package tools;


import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;


import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;

import static tools.AnalysisMat.getBorder;


public class GetPicture {

    public static void getPicture(String videoPath,
                                  String picPath){
        Mat srcMat = new Mat();
        Mat grayMat = new Mat();

        File file = new File(videoPath);
        //判断对象是否存在，不存在的话给出Toast提示
        if (file.exists()) {
            //提供统一的接口用于从一个输入媒体中取得帧和元数据
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoPath);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//获取视频的时长，以确定取多上张图片

            int a = 40;
            int size = Integer.valueOf(time) / a; //每秒25帧


            //帧图片保存
            for (int i = 1; i < size; i++) {
                Bitmap bitmap = retriever.getFrameAtTime(i * 1000 * a, MediaMetadataRetriever.OPTION_CLOSEST );
                Utils.bitmapToMat(bitmap,srcMat); //转换成mat
                Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_BGR2GRAY);  // 灰度化

                String pathPic = picPath + i + ".jpg";
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(pathPic);
                    getBorder(srcMat,grayMat);  // 绘制外边界
                    Utils.matToBitmap(srcMat,bitmap);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);//保存
                    fos.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
