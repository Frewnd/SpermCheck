package tools;

import com.example.spermcheck.R;

import java.io.UnsupportedEncodingException;

import static tools.DateUtil.getDay;
import static tools.DateUtil.getMonth;
import static tools.RWResultAsTxt.saveFileInformation;


public class JudgeResult {
    public static int [] judge(String[] arr, //原数组，包含颜色、体积等信息
                               String[] result, // 结果输数数组
                               String score_path, // 分数保存路径
                               String advice_path, // 建议保存路径
                               String[] distance_temp)// 精子运动总路程路径
    {
        int color[] = new int [8]; // 背景颜色集合
        int num [] = new int [arr.length]; // 临时数组，判断每个参数是否异常
        double temp [] = new double[arr.length + 1]; // 临时数组，保存每项检测的分数值
        String string = " "; // 保存所有检测的中文结果
        String advice = " "; // 保存所有检测医生建议
        double score = 0; // 统计检测的总分数
        result[0] = getMonth()+"月"+getDay(); // 保存检测日期
        int pr = 0; //前向运动


        // 判断运动类型
        double distance = 0; // 临时变量，将distance的String转化double类型
        for(int i=0; i<distance_temp.length;i++){
            if (distance_temp[i] != "") {
                distance = Double.parseDouble(distance_temp[i]);
                if (distance > 0.53) {
                    pr++;
                }
            }
        }

        //判断外观
        String str = "白";
        String str1 = "黄";
        try {
            if (new String(arr[0].getBytes(),"utf-8").indexOf(new String(str.getBytes(),"utf-8")) != -1 ||
                new String(arr[0].getBytes(),"utf-8").indexOf(new String(str1.getBytes(),"utf-8")) != -1){
                color[0] = R.color.green;
                result[1] = "颜色正常";
                num [0] = 1;
                temp[1] = 10;
                score = score + temp[1];

            }else {
                color[0] = R.color.red;
                result[1] = "您的精液颜色异常，建议您重新取样进行测试，如果仍出现类似结果，请您尽早咨询医生做进一步确诊。";
                num [0] = 0;
                temp[1] = 0;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //判断体积
        double a = Double.parseDouble(arr[1])-1.5;
        double b = Double.parseDouble(arr[1])-6.8;
        if (a>=0){
            color[1] = R.color.green;
            result[2] = "精液量在正常范围内";
            num [1] = 1;
            temp[2] = Double.parseDouble(String.format("%.1f", (Double.parseDouble(arr[1])/4)*30));
            if (temp[2]>10){
                temp[2] = 10;
            }
            score = score + temp[2];
        }else {
            color[1] = R.color.red;
            result[2] = "您的精液量异常，正常参考值在1.5~6.8毫升之间，建议您尽早咨询医生做进一步确诊。";
            num [1] = 0;
            temp[2] = 0;
        }


        //判断禁欲时间
         double c =Double.parseDouble(arr[2]);
        if(c<3){
            color[2] = R.color.red;
            num [2] = 0;
            result[3] = "精子的活力和染色体不受禁欲时间长短的影响，但可能会影响精子浓度。";
        }else {
            color[2] = R.color.green;
            result[3] = "禁欲时间在正常达范围内。";
            num [2] = 1;
        }
        temp[3] =  Double.parseDouble(String.format("%.1f",(7/Double.parseDouble(arr[2]))*10));
        if (temp[3]>10){
            temp[3] = 10;
        }
        score = score + temp[3];



        //判断存活率
        double d = Double.parseDouble(arr[3]);
        if (d>=32){
            color[3] = R.color.green;
            result[4] = "精子存活率正常";
            num [3] = 1;
            temp[4] = Double.parseDouble(String.format("%.1f",(d/32)*10));
            if (temp[4]>30){
                temp[4]=30;
            }
            score = score + temp[4];
        }else {
            color[3] = R.color.red;
            result[4] = "您的精子存活率检测结果低于正常参考值下限（正常值大于32%），建议您尽早咨询医生做进一步确诊。";
            num [3] = 0;
            temp[4] = 0;
        }

        //判断精子浓度
        double f  = Double.parseDouble(arr[4]);
        int ff = (int)(Double.parseDouble(arr[4])/1.76);
        if (ff>=15){
            color[4] = R.color.green;
            result[5] = "精子浓度正常";
            num [4] = 1;
        }else {
            color[4] = R.color.red;
            result[5] = "您的精子总数检测结果低于正常参考值下限【(15 ~213)百万/ml】，建议您尽早咨询医生做进一步确诊。";
            num [4] = 0;
        }
        temp[5] = Double.parseDouble(String.format("%.1f",(f/50)*10));
        if (temp[5]>20||score!=0){
            temp[5] = 20;
            score = score + temp[5];
        }


        temp[0] = temp[1] +temp[2] +temp[3] +temp[4] + temp[5]; // 总分数


        for(int i=0;i<num.length;i++){
            switch (i){
                case 0:{
                    if (num[i]==0)
                        string = "颜色异常、";
                }break;
                case 1:{
                    if (num[i]==0)
                        string = string +"体积异常、";
                }break;
                case 2:{
                    if (num[i]==0)
                        string = string +"禁欲时间异常、";
                }break;
                case 3:{
                    if (num[i]==0)
                        string = string +"活力异常、";
                }break;
                case 4:{
                    if (num[i]==0)
                        string = string +"精子总数异常。";
                }break;
            }
        }
        if (string==" "){
            string = "您本次检测结果正常。";
            advice = advice + "请继续保持！";
        }else{
            string = "您本次检测结果"+string+"您可以查看下面报告中异常的指标，如有困惑，可以在线咨询医生获取帮助。"+"\n";
            advice = advice +"精液检测需用同一设备连续检测2~3次结果才更加准确，每次检测时间间隔宜1~2周。"+"\n";
        }

        //判断前向运动率是否合格
        double pr_percent = pr/f * 100;
        if (pr_percent >= 40){
            color[5] = R.color.green;
        }else {
            color[5] = R.color.red;
        }

        //判断非前向运动率是否合格
        double nr_percent = d - pr_percent;
        if (nr_percent >= 10 && nr_percent <= 30){
            color[6] = R.color.green;
        }else {
            color[6] = R.color.red;
        }

        //判断不动率是否合格
        double im_percent = 100 - d;
        if (im_percent <= 30){
            color[7] = R.color.green;
        }else {
            color[7] = R.color.red;
        }
        arr[1] = arr[1] + "毫升";
        arr[2] = arr[2] + "天";
        arr[3] = arr[3] + "%";
//        arr[3] = 85 + "%";
        arr[4] = ff + "百万/毫升";
        arr[5] = String.format("%.1f", pr_percent) + "%";
        arr[6] = String.format("%.1f", nr_percent)+ "%";
        arr[7] = im_percent + "%";


        result[5] = string;
        result[6] = advice;
        result[7] = String.format("%.1f",score);

        String result_score = temp[0] + "," + temp[1] + "," + temp[2] + "," + temp[3] + "," + temp[4] + "," + temp[5];
        String result_advice = result[0] + "-" + result[1] + "-" + result[2] + "-" + result[3] + "-" + result[4] + "-" + result[5] + '-' + result[6];     //以“-”号作为分隔符，不与内容的“，”冲突，方便后续切片
        saveFileInformation(result_score, score_path);
        saveFileInformation(result_advice, advice_path);
        return color;
    }
}
