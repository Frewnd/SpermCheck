package tools;

public class JudgeTotalResult {
    public static String analysis(float [] score){
        String advice = "统计来看：\n";
        int length = score.length;

        //最近一次检测结果分析
        if(score[length-1]>=60&&score[length-1]<80){
            advice = advice +"您最近一次的结果是合格的，能达到备孕的最低要求。\n";
        }else if (score[length-1]>=80){
            advice = advice +"您最近一次的结果非常优秀，已经达到优质备孕的标准。\n";
        }else{
            advice = advice +"您最近一次的结果是不合格的，未能达到备孕的最低要求，请继续加油哦。\n";
        }

//       //最近三次检测结果分析
//        float sum = score[length-1] + score[length-2] + score[length-3];
//        if (sum>=200){
//            advice = advice +"您最近三次的结果都为合格，未能达到备孕的最低要求，请继续加油哦。\n";
//        }
        return advice;
    }
}
