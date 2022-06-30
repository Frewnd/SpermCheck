package tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetTime {
    public static String getNowTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String string = simpleDateFormat.format(date);
        return string;
    }
    public static String getNowId(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String string = simpleDateFormat.format(date);
        return string;
    }

    public static String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String string = simpleDateFormat.format(date);
        return string;
    }

}
