package tools;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author： Lawrence
 * date：  6/26/2020--9:26 PM
 * tel：  18800273121
 * e-mail：  xbstyles@163.com
 * version：  1.0
 * 功能介绍 ： 获取某一文件夹下所有的文件名
 */
public class GetFileAllName {
    public static List<String> getFilesAllName(File file) {
        File[] files=file.listFiles();
        if (files == null){
            Log.e("错误","空目录");return null;}
        List<String> s = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            s.add(files[i].getName());
        }
        return s;
    }
}
