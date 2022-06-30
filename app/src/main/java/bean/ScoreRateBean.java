package bean;

public class ScoreRateBean {
    /*
    分数
     */
    private float score;

    /*
    日期
     */
    private String date;

    public float getScore(){
        return score;
    }

    public void setScore(){
        this.score = score;
    }

    public String getDate(){
        return date;
    }

    public void setDate(){
        this.date = date;
    }

    public ScoreRateBean(float score,String date){
        this.score = score;
        this.date = date;
    }
}
